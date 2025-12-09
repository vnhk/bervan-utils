package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.bervan.ieentities.BaseExcelExport.LARGE_TEXT_PARTS_SHEET;

@Slf4j
public class BaseExcelImport {
    private final Map<Class<?>, Sheet> sheets = new HashMap<>();
    private final List entities = new ArrayList<>();
    private final List<Class<?>> classesSupportsImport;
    private Sheet largeTextPartsSheet;

    public BaseExcelImport(List<Class<?>> classesSupportsImport) {
        this.classesSupportsImport = classesSupportsImport;
    }

    private double getNumericCellValue(Cell cell) {
        return cell.getNumericCellValue();
    }

    public Workbook load(String dirPath, String fileName) {
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
        String fileLocation = path + File.separator + fileName;

        try (FileInputStream inputStream = new FileInputStream(fileLocation)) {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load workbook!", e);
        }
    }

    public Workbook load(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load workbook!", e);
        }
    }

    public Workbook load(FileInputStream fileInputStream) {
        try {
            return new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load workbook!", e);
        }
    }

    // TODO: 30/07/2023 Add additional excel row id column used for identifying processed rows instead on using and requiring entity id
    //  - it would allow to import data without requirement of providing entity Id
    public List<?> importExcel(Workbook workbook) {
        entities.clear();
        sheets.clear();
        loadSheets(workbook);
        try {
            for (Map.Entry<Class<?>, Sheet> classSheetEntry : sheets.entrySet()) {
                importData(classSheetEntry.getKey(), classSheetEntry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Import failed!", e);
        }
        return entities;
    }

    protected void importData(Class<?> entity, Sheet sheet) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        int lastRowNum = getLastRowNum(sheet);
        log.info("Importing " + sheet.getSheetName() + "....");
        log.info("Rows to be imported: " + lastRowNum);
        List<String> headerNames = getHeaderNames(sheet);
        List<Field> fieldsToImportData = getFieldsToImportData(entity, headerNames);
        Map<String, Field> fieldsForColumn = new HashMap<>();
        for (String headerName : headerNames) {
            fieldsForColumn.put(headerName, getFieldForHeaderName(fieldsToImportData, headerName));
        }

        int i = 1;
        for (; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ExcelIEEntity<?> excelIEEntity = initEntity(entity);
                fillData(excelIEEntity, row, headerNames, fieldsForColumn, sheet);
                entities.add(excelIEEntity);
            } else {
                log.debug("Processing ended on row: " + i);
                break;
            }
        }
    }

    private int getLastRowNum(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        //0 is HEADER
        int curr = 1;
        for (; curr <= lastRowNum; curr++) {
            try {
                //if value in 1 cell is < 1 then we don't have rows to process
                if (sheet.getRow(curr).getCell(0).getNumericCellValue() < 1) {
                    return curr;
                }
            } catch (Exception e) {
                return curr;
            }
        }
        return lastRowNum;
    }

    private Field getFieldForHeaderName(List<Field> fieldsToImportData, String headerName) {
        log.debug("getFieldForHeaderName: headerName=" + headerName);
        return fieldsToImportData.stream().filter(e -> e.getName().equalsIgnoreCase(headerName)).findFirst().get();
    }

    private void fillData(ExcelIEEntity<?> entity, Row row, List<String> headerNames, Map<String, Field> fields, Sheet sheet) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        for (int i = 0; i < headerNames.size(); i++) {
            String columnName = headerNames.get(i);
            Cell cell = row.getCell(i + 1);
            if (cell != null) {
                setValue(cell, entity, fields.get(columnName));
            }
        }
    }

    protected List<Field> getFieldsToImportData(Class<?> entity, List<String> headersName) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> headersName.contains(e.getName().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    protected String rebuildStringFromParts(String keyReference) {
        StringBuilder rebuiltString = new StringBuilder();

        for (Row row : largeTextPartsSheet) {
            Cell keyCell = row.getCell(0);
            if (keyCell != null && keyCell.getStringCellValue().startsWith(keyReference)) {
                Cell valueCell = row.getCell(1);
                if (valueCell != null) {
                    rebuiltString.append(valueCell.getStringCellValue());
                }
            }
        }

        return rebuiltString.toString();
    }

    protected List<String> getHeaderNames(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = headerRow.getLastCellNum();
        List<String> headersNames = new ArrayList<>();
        for (int i = 1; i < lastCellNum; i++) {
            headersNames.add(headerRow.getCell(i).getStringCellValue().toLowerCase(Locale.ROOT));
        }

        return headersNames;
    }

    protected ExcelIEEntity<?> initEntity(Class<?> cl) {
        try {
            return (ExcelIEEntity<?>) cl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Entity must have no args constructor!", e);
        }
    }

    protected void loadSheets(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            largeTextPartsSheet = workbook.getSheet(LARGE_TEXT_PARTS_SHEET);
            Optional<Class<?>> classToImport = classesSupportsImport.stream().filter(cl -> cl.getSimpleName().equals(sheet.getSheetName()))
                    .findFirst();

            if (classToImport.isPresent()) {
                sheets.put(classToImport.get(), sheet);
                log.debug("Sheet: " + sheet.getSheetName() + " will be used for importing data for " + classToImport.get().getName());
            } else {
                log.warn("Could not find available class for class name:" + sheet.getSheetName() + ". Skipping.");
            }
        }

        log.info("Found " + sheets.size() + " to be imported!");
    }

    protected void setValue(Cell cell, ExcelIEEntity<?> entity, Field field)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        String typeName = field.getType().getTypeName();
        String parametrizedType = null;
        int parametrizedSignStart = 0;
        int parametrizedSignEnd = 0;
        if (typeName.contains("<") && typeName.contains(">")) {
            parametrizedSignStart = typeName.indexOf("<") + 1;
            parametrizedSignEnd = typeName.indexOf(">");
            parametrizedType = typeName.substring(parametrizedSignStart, parametrizedSignEnd);
        }

        field.setAccessible(true);

        if (typeName.equals(Boolean.class.getTypeName())) {
            field.set(entity, cell.getBooleanCellValue());
        } else if (Enum.class.isAssignableFrom(field.getType())) {
            Class<? extends Enum> type = (Class<? extends Enum>) field.getType();
            field.set(entity, Enum.valueOf(type, cell.getStringCellValue()));
        } else if (typeName.equals(Double.class.getTypeName())) {
            try {
                double numericCellValue = cell.getNumericCellValue();
                field.set(entity, numericCellValue);
            } catch (Exception e) {
                String numericCellValue = cell.getStringCellValue();
                field.set(entity, Double.valueOf(numericCellValue));
            }
        } else if (typeName.equals(BigDecimal.class.getTypeName())) {
            if (cell.getCellType() == CellType.NUMERIC) {
                field.set(entity, BigDecimal.valueOf(cell.getNumericCellValue()));
            } else {
                String stringCellValue = cell.getStringCellValue();
                field.set(entity, new BigDecimal(stringCellValue));
            }
        } else if (typeName.equals(Integer.class.getTypeName())) {
            try {
                double numericCellValue = cell.getNumericCellValue();
                field.set(entity, Double.valueOf(numericCellValue).intValue());
            } catch (Exception e) {
                String numericCellValue = cell.getStringCellValue();
                field.set(entity, Double.valueOf(numericCellValue).intValue());
            }
        } else if (typeName.equals(Long.class.getTypeName())) {
            try {
                double numericCellValue = cell.getNumericCellValue();
                field.set(entity, Double.valueOf(numericCellValue).longValue());
            } catch (Exception e) {
                String numericCellValue = cell.getStringCellValue();
                field.set(entity, Double.valueOf(numericCellValue).longValue());
            }
        } else if (typeName.equals(Date.class.getTypeName())) {
            field.set(entity, cell.getDateCellValue());
        } else if (typeName.equals(LocalDateTime.class.getTypeName())) {
            LocalDateTime result = null;

            if (cell.getCellType() == CellType.NUMERIC) {
                // Excel date -> LocalDateTime
                result = cell.getLocalDateTimeCellValue();

            } else if (cell.getCellType() == CellType.STRING) {
                // String "dd/MM/yyyy HH:mm:ss" -> LocalDateTime
                String raw = cell.getStringCellValue();
                if (raw != null && !raw.isBlank()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    result = LocalDateTime.parse(raw.trim(), formatter);
                }
            }

            field.set(entity, result);
        } else if (typeName.equals(LocalDate.class.getTypeName())) {
            LocalDate result = null;

            if (cell.getCellType() == CellType.NUMERIC) {
                // Excel date -> LocalDate
                LocalDateTime ldt = cell.getLocalDateTimeCellValue();
                result = ldt != null ? ldt.toLocalDate() : null;

            } else if (cell.getCellType() == CellType.STRING) {
                // String "dd/MM/yyyy" -> LocalDate
                String raw = cell.getStringCellValue();
                if (raw != null && !raw.isBlank()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    result = LocalDate.parse(raw.trim(), formatter);
                }
            }

            field.set(entity, result);
        } else if (isCollectionOfExcelEntities(typeName, parametrizedType, parametrizedSignStart, parametrizedSignEnd)) {
            String value = cell.getStringCellValue();
            if (Strings.isNotBlank(value)) {
                List<ExcelIEEntity<?>> excelIEEntities = new ArrayList<>();
                String[] ids = value.split(",");
                Class<?> classExcelEntity = Class.forName(parametrizedType);
                ExcelIEEntity<?> excelEntityRef = initEntity(classExcelEntity);
                Field idField = Arrays.stream(excelEntityRef.getClass().getDeclaredFields())
                        .filter(e -> e.getName().equalsIgnoreCase("Id")).findFirst().get();
                for (String id : ids) {
                    setExcelEntityRefId(excelEntityRef, id.trim(), idField);
                    excelIEEntities.add(excelEntityRef);
                }
                field.set(entity, excelIEEntities);

                //????
//                if (typeName.replace(parametrizedType, "").equals(List.class.getTypeName())) {
//                    LocalDateTime localDateTimeCellValue = cell.getLocalDateTimeCellValue();
//                    field.set(entity, localDateTimeCellValue != null ? localDateTimeCellValue.toLocalDate() : null);
//                }
            }
        } else if (typeName.equals(UUID.class.getTypeName())) {
            String value = cell.getStringCellValue();
            field.set(entity, UUID.fromString(value));
        } else if (typeName.equals(String.class.getTypeName())) {
            String value = cell.getStringCellValue();
            if (value.startsWith("LargeTextParts_")) {
                value = rebuildStringFromParts(value);
            }
            field.set(entity, value);
        } else if (ExcelIEEntity.class.isAssignableFrom(field.getType())) {
            Class<?> classExcelEntity = Class.forName(typeName);
            ExcelIEEntity<?> excelEntityRef = initEntity(classExcelEntity);
            Field idField = Arrays.stream(excelEntityRef.getClass().getDeclaredFields())
                    .filter(e -> e.getName().equalsIgnoreCase("Id")).findFirst().get();
            setExcelEntityRefId(excelEntityRef, cell.getStringCellValue().trim(), idField);
            field.set(entity, excelEntityRef);
        }
        field.setAccessible(false);

    }

    protected void setExcelEntityRefId(ExcelIEEntity<?> excelEntityRef, String id, Field idField) throws InvocationTargetException, IllegalAccessException {
        idField.setAccessible(true);
        String typeName = idField.getType().getName();
        if (typeName.equals(String.class.getTypeName())) {
            idField.set(excelEntityRef, id);
            return;
        } else if (typeName.equals(Long.class.getTypeName())) {
            idField.set(excelEntityRef, Long.parseLong(id));
            return;
        } else if (typeName.equals(Integer.class.getTypeName())) {
            idField.set(excelEntityRef, Integer.parseInt(id));
            return;
        } else if (typeName.equals(UUID.class.getTypeName())) {
            idField.set(excelEntityRef, UUID.fromString(id));
            return;
        } else {
            defaultMapper(excelEntityRef, id, idField, typeName);
        }
        idField.setAccessible(false);

        throw new RuntimeException("Could not set excel entity ref id!");
    }

    protected void defaultMapper(ExcelIEEntity<?> excelEntityRef, String id, Field idField, String typeName) {

    }

    private boolean isCollectionOfExcelEntities(String typeName,
                                                String parametrizedTypeName,
                                                int parametrizedSignStart,
                                                int parametrizedSignEnd) throws ClassNotFoundException {
        boolean parametrizedCollection = parametrizedTypeName != null && parametrizedSignEnd != 0 && parametrizedSignStart != 0
                && (typeName.contains(List.class.getTypeName()) || typeName.contains(Set.class.getTypeName()));

        if (parametrizedCollection) {
            Class<?> aClass = Class.forName(parametrizedTypeName);
            return ExcelIEEntity.class.isAssignableFrom(aClass);
        }

        return false;
    }
}
