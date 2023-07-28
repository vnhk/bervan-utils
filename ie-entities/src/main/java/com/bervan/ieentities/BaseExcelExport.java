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
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseExcelExport {
    private final Map<String, Integer> columnIndexForField = new HashMap<>();
    private final Map<String, Integer> lastColumnIndexForSheet = new HashMap<>();
    private final Map<Class<? extends ExcelIEEntity<?>>, List<Object>> processedEntities = new HashMap<>();
    private Workbook workbook;

    public void save(Workbook workbook, String dirPath, String fileName) {
        if (Strings.isBlank(dirPath)) {
            dirPath = ".";
            log.warn("Directory path is empty. Workbook will be saved in current directory.");
        }

        if (Strings.isBlank(fileName)) {
            fileName = "temp";
            log.warn("Filename is empty. Workbook will be saved as temp.xlsx.");
        }


        File currDir = new File(dirPath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + fileName + ".xlsx";

        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error("Cannot save workbook!", e);
        }
    }

    public Workbook exportExcel(List<? extends ExcelIEEntity<?>> entities, Workbook workbook) {
        this.workbook = Objects.requireNonNullElseGet(workbook, XSSFWorkbook::new);

        for (ExcelIEEntity<?> entity : entities) {
            if (entity.getId() == null) {
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
            row.createCell(0).setCellValue(getIdHeaderName());
            int columnCount = 1;
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
        sheet.getRow(rowNumber).createCell(0).setCellValue(entity.getId().toString());
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
            throw new RuntimeException(e);
        }
    }

    protected void setCellValue(Sheet sheet, Integer columnIndex, Integer rowIndex, Object value) {
        if (value != null) {
            Cell cell = sheet.getRow(rowIndex).createCell(columnIndex);
            if (value instanceof String) {
                cell.setCellValue(value.toString());
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
                        log.warn("Value is a non ExcelEntity collection. Will not be processed. Create custom exporter!");
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
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> !e.getName().equals("getId"))
                .filter(e -> e.getName().startsWith("get"))
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
                lastUsedColumnIndex = 1;
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
