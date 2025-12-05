package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseExcelExport {
    public static final String LARGE_TEXT_PARTS_SHEET = "LargeTextParts";
    private final Map<String, Integer> columnIndexForField = new HashMap<>();
    private final Map<String, Integer> lastColumnIndexForSheet = new HashMap<>();
    private final Map<Class<? extends ExcelIEEntity<?>>, List<Object>> processedEntities = new HashMap<>();
    private final int MAX_TEXT_LENGTH = 30000;
    private Workbook workbook;

    public File save(Workbook workbook, String dirPath, String fileName) {
        if (Strings.isBlank(dirPath)) {
            dirPath = ".";
//            log.warn("Directory path is empty. Workbook will be saved in current directory.");
        }

        if (Strings.isBlank(fileName)) {
            fileName = "temp";
//            log.warn("Filename is empty. Workbook will be saved as temp.xlsx.");
        }


        File currDir = new File(dirPath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + File.separator + fileName;
        File toSave = new File(fileLocation);

        try (FileOutputStream outputStream = new FileOutputStream(toSave)) {
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error("Cannot save workbook!", e);
            throw new RuntimeException("Cannot save workbook!", e);
        }

        return toSave;
    }

    public Workbook exportExcel(List<? extends ExcelIEEntity<?>> entities, Workbook workbook) {
        log.info("Exporting " + entities.size() + " entities.");
        this.workbook = Objects.requireNonNullElseGet(workbook, XSSFWorkbook::new);
        this.processedEntities.clear();
        this.columnIndexForField.clear();
        this.lastColumnIndexForSheet.clear();
        buildLargeTextPartsSheet();

        for (ExcelIEEntity<?> entity : entities) {
            if (entity.getId() == null) {
                log.error("Could not export entities with id = null!");
                throw new RuntimeException("Could not export entities with id = null!");
            }

            if (processedEntities.get(entity.getClass()) != null && processedEntities.get(entity.getClass()).contains(entity.getId())) {
                log.info("Entity " + entity.getClass().getSimpleName() + " with id = " + entity.getId() + " already exported. Skip.");
                continue;
            }

            appendProcessedEntity(entity);

            processEntity(entity);
        }

        return this.workbook;
    }

    private void buildLargeTextPartsSheet() {
        Sheet largeTextRefs = getSheet(LARGE_TEXT_PARTS_SHEET);
        Row row = largeTextRefs.createRow(0);
        row.createCell(0).setCellValue("RefKeyPart");
        row.createCell(1).setCellValue("PartValue");
    }

    private void appendProcessedEntity(ExcelIEEntity<?> entity) {
        List<Object> processedIds = processedEntities.get(entity.getClass());
        if (processedIds == null) {
            processedIds = new ArrayList<>();
        }
        processedIds.add(entity.getId());

        processedEntities.put((Class<? extends ExcelIEEntity<?>>) entity.getClass(), processedIds);
    }

    protected void processEntity(ExcelIEEntity<?> entity) {
        String entityName = entity.getClass().getSimpleName();
        Sheet sheet = getSheet(entityName);
        List<Method> getters = getGettersToExportData(entity);
        createHeaders(sheet, getters);
        createCellWithId(entity, sheet);

        for (Method getter : getters) {
            exportField(sheet, entity, getter);
        }

        nextRowNumber(sheet);
    }

    private void createHeaders(Sheet sheet, List<Method> getters) {
        Row row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("#");
            row.createCell(1).setCellValue(getIdHeaderName());
            int columnCount = 2;
            for (Method getter : getters) {
                String fieldName = getter.getName().split("get")[1];
                row.createCell(columnCount).setCellValue(fieldName);
                columnCount++;
            }
        }
    }

    protected String getIdHeaderName() {
        return "Id";
    }

    protected void createCellWithId(ExcelIEEntity<?> entity, Sheet sheet) {
        Integer rowNumber = getRowNumber(sheet);
        sheet.getRow(rowNumber).createCell(0).setCellValue(rowNumber);
        sheet.getRow(rowNumber).createCell(1).setCellValue(entity.getId().toString());
    }

    protected void nextRowNumber(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        sheet.createRow(lastRowNum + 1);
    }

    protected void exportField(Sheet sheet, ExcelIEEntity<?> entity, Method getter) {
        String fieldName = getter.getName().split("get")[1];
        try {
            Object val = getVal(entity, getter);
            Integer columnIndex = getColumnNumber(sheet, fieldName);
            Integer rowIndex = getRowNumber(sheet);
            setCellValue(sheet, columnIndex, rowIndex, val);
        } catch (Exception e) {
            log.error("Could not export " + fieldName + "!", e);
            throw new RuntimeException("Could not export " + fieldName + "!", e);
        }
    }

    protected void setCellValue(Sheet sheet, Integer columnIndex, Integer rowIndex, Object value) {
        if (value != null) {
            Cell cell = sheet.getRow(rowIndex).createCell(columnIndex);
            if (value instanceof String) {
                cell.setCellValue(processStringIfLarge(sheet, columnIndex, rowIndex, value));
            } else if (value instanceof Enum) {
                cell.setCellValue(((Enum<?>) value).name());
            } else if (value instanceof Boolean) {
                cell.setCellValue(((Boolean) value));
            } else if (value instanceof Date) {
                cell.setCellValue(((Date) value));
            } else if (value instanceof LocalDateTime) {
                cell.setCellValue(((LocalDateTime) value));
            } else if (value instanceof LocalDate) {
                cell.setCellValue(((LocalDate) value));
            } else if (value instanceof Double) {
                cell.setCellValue(((Double) value));
            } else if (value instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) value).toString());
            } else if (value instanceof Long) {
                cell.setCellValue(((Long) value));
            } else if (value instanceof Integer) {
                cell.setCellValue(((Integer) value));
            } else if (value instanceof ExcelIEEntity) {
                cell.setCellValue(String.valueOf(((ExcelIEEntity<?>) value).getId()));
                exportExcel(Collections.singletonList(((ExcelIEEntity<?>) value)), workbook);
            } else if (value instanceof Collection && ((Collection<?>) value).size() > 0) {
                StringBuilder sb = new StringBuilder();
                Iterator<?> iterator = ((Collection<?>) value).iterator();
                Class<?> elementClass = null;
                while (iterator.hasNext()) {

                    Object next = iterator.next();
                    if (elementClass == null) {
                        elementClass = next.getClass();
                    }

                    if (elementClass == next.getClass() && next instanceof ExcelIEEntity) {
                        sb.append(((ExcelIEEntity<?>) next).getId());
                        sb.append(",");
                    } else {
//                        log.warn("Value is a non ExcelEntity collection. Will not be processed. Create custom exporter!");
                        return;
                    }
                }

                if (sb.length() > 0) {
                    String idsSeparatedByComma = sb.toString();
                    idsSeparatedByComma = idsSeparatedByComma.substring(0, idsSeparatedByComma.length() - 1);
                    cell.setCellValue(idsSeparatedByComma);
                }
            }
        }
    }

    private String processStringIfLarge(Sheet ownerSheet, Integer columnIndex, Integer rowIndex, Object value) {
        String string = value.toString();
        if (string.length() > MAX_TEXT_LENGTH) {
//            log.info("Text value is to big to be exported to one cell because of the excel limit.");
            int neededParts = string.length() / MAX_TEXT_LENGTH;
            if (neededParts * MAX_TEXT_LENGTH < string.length()) {
                neededParts++;
            }
//            log.info("Text will be divided into " + neededParts + " parts.");
            Sheet sheet = getSheet(LARGE_TEXT_PARTS_SHEET);
            String keyReference = LARGE_TEXT_PARTS_SHEET + "_" + ownerSheet.getSheetName() + "_" + columnIndex + "_" + rowIndex + "_";
            for (int i = 0; i < neededParts; i++) {
                int lastRowNum = sheet.getLastRowNum();
                Row row = sheet.createRow(lastRowNum + 1);
                row.createCell(0).setCellValue(keyReference + i);
                int beginIndex = i * MAX_TEXT_LENGTH;
                int endIndex = beginIndex + MAX_TEXT_LENGTH;
                if (endIndex > string.length()) {
                    endIndex = string.length();
                }
                row.createCell(1).setCellValue(string.substring(beginIndex, endIndex));
            }
            return keyReference;
        }

        return string;
    }

    protected Integer getRowNumber(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum == 0) {
            sheet.createRow(1);
            lastRowNum = 1;
        }
        return lastRowNum;
    }

    protected Sheet getSheet(String name) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            return workbook.createSheet(name);
        } else {
            return sheet;
        }
    }

    protected List<Method> getGettersToExportData(Object object) {
        Set<String> fields = Arrays.stream(object.getClass().getDeclaredFields())
                .map(Field::getName).map(String::toLowerCase).collect(Collectors.toSet());
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> !e.getName().equals("getId"))
                .filter(e -> e.getName().startsWith("get"))
                .filter(e -> fields.contains(e.getName().replace("get", "").toLowerCase()))
                .collect(Collectors.toList());
    }

    protected Object getVal(ExcelIEEntity<?> entity, Method getter) throws Exception {
        return getter.invoke(entity);
    }

    protected Integer getColumnNumber(Sheet sheet, String fieldName) {
        String key = sheet.getSheetName() + ":" + fieldName;
        Integer columnIndex = columnIndexForField.get(key);
        if (columnIndex == null) {
            String lastColumnIndexForSheetKey = sheet.getSheetName() + ":" + sheet.getLastRowNum();
            Integer lastUsedColumnIndex = lastColumnIndexForSheet.get(lastColumnIndexForSheetKey);
            if (lastUsedColumnIndex == null) {
                lastUsedColumnIndex = 2; //[#,id,X]
            } else {
                lastUsedColumnIndex++;
            }
            lastColumnIndexForSheet.put(lastColumnIndexForSheetKey, lastUsedColumnIndex);
            columnIndex = lastUsedColumnIndex;
            columnIndexForField.put(key, columnIndex);
        }

        return columnIndex;
    }
}
