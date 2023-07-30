package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseExcelImport {
    private final Map<Class<? extends ExcelIEEntity<?>>, List<Object>> processedEntities = new HashMap<>();
    private final Map<Class<?>, Sheet> sheets = new HashMap<>();
    private final List entities = new ArrayList<>();
    private final List<Class<?>> classesSupportsImport;

    public BaseExcelImport(List<Class<?>> classesSupportsImport) {
        this.classesSupportsImport = classesSupportsImport;
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
        String fileLocation = path.substring(0, path.length() - 1) + fileName + ".xlsx";

        try (FileInputStream inputStream = new FileInputStream(fileLocation)) {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            log.error("Cannot load workbook!", e);
            throw new RuntimeException(e);
        }
    }

    public Workbook load(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            log.error("Cannot load workbook!", e);
            throw new RuntimeException(e);
        }
    }

    // TODO: 30/07/2023 Add additional excel row id column used for identifying processed rows instead on using and requiring entity id
    //  - it would allow to import data without requirement of providing entity Id
    public List<?> importExcel(Workbook workbook) {
        loadSheets(workbook);
        try {
            for (Map.Entry<Class<?>, Sheet> classSheetEntry : sheets.entrySet()) {
                importData(classSheetEntry.getKey(), classSheetEntry.getValue());
            }
        } catch (Exception e) {
            log.error("Import failed!", e);
            throw new RuntimeException(e);
        }
        return entities;
    }

    protected void importData(Class<?> entity, Sheet sheet) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        int lastRowNum = sheet.getLastRowNum();
        List<String> headerNames = getHeaderNames(sheet);
        List<Method> settersToImportData = getSettersToImportData(entity, headerNames);
        Map<String, Method> setterForColumn = new HashMap<>();
        for (String headerName : headerNames) {
            setterForColumn.put(headerName, getSetterForHeaderName(settersToImportData, headerName));
        }

        for (int i = 1; i < lastRowNum; i++) {
            ExcelIEEntity<?> excelIEEntity = initEntity(entity);
            fillData(excelIEEntity, i, headerNames, setterForColumn, sheet);
            entities.add(excelIEEntity);
        }
    }

    private Method getSetterForHeaderName(List<Method> settersToImportData, String headerName) {
        return settersToImportData.stream().filter(e -> e.getName().equals("set" + headerName)).findFirst().get();
    }

    private void fillData(ExcelIEEntity<?> entity, int rowNumber, List<String> headerNames, Map<String, Method> setters, Sheet sheet) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        log.info("Importing row: " + rowNumber + " for " + sheet.getSheetName());
        Row row = sheet.getRow(rowNumber);
        for (int i = 1; i < headerNames.size(); i++) {
            String columnName = headerNames.get(i);
            Cell cell = row.getCell(i);
            if (cell != null) {
                useSetterToSetValue(cell, entity, setters.get(columnName));
            }
        }
    }

    protected List<Method> getSettersToImportData(Class<?> entity, List<String> headersName) {
        return Arrays.stream(entity.getDeclaredMethods())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> e.getName().startsWith("set"))
                .filter(e -> headersName.contains(e.getName().substring(3)))
                .collect(Collectors.toList());
    }


    protected List<String> getHeaderNames(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = headerRow.getLastCellNum();
        List<String> headersNames = new ArrayList<>();
        for (int i = 0; i < lastCellNum; i++) {
            headersNames.add(headerRow.getCell(i).getStringCellValue());
        }

        return headersNames;
    }

    protected ExcelIEEntity<?> initEntity(Class<?> cl) {
        try {
            return (ExcelIEEntity<?>) cl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Entity must have no args constructor!", e);
            throw new RuntimeException(e);
        }
    }

    protected void loadSheets(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Optional<Class<?>> classToImport = classesSupportsImport.stream().filter(cl -> cl.getSimpleName().equals(sheet.getSheetName()))
                    .findFirst();

            if (classToImport.isPresent()) {
                sheets.put(classToImport.get(), sheet);
            } else {
                log.warn("Could not find available class for class name:" + sheet.getSheetName() + ". Skipping.");
            }
        }
    }

    protected void useSetterToSetValue(Cell cell, ExcelIEEntity<?> entity, Method setter)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        String typeName = setter.getParameters()[0].getParameterizedType().getTypeName();
        String parametrizedType = null;
        int parametrizedSignStart = 0;
        int parametrizedSignEnd = 0;
        if (typeName.contains("<") && typeName.contains(">")) {
            parametrizedSignStart = typeName.indexOf("<") + 1;
            parametrizedSignEnd = typeName.indexOf(">");
            parametrizedType = typeName.substring(parametrizedSignStart, parametrizedSignEnd);
        }

        if (typeName.equals(Boolean.class.getTypeName())) {
            setter.invoke(entity, cell.getBooleanCellValue());
        } else if (typeName.equals(Double.class.getTypeName())) {
            setter.invoke(entity, cell.getNumericCellValue());
        } else if (typeName.equals(Integer.class.getTypeName())) {
            double numericCellValue = cell.getNumericCellValue();
            setter.invoke(entity, Double.valueOf(numericCellValue).intValue());
        } else if (typeName.equals(Long.class.getTypeName())) {
            double numericCellValue = cell.getNumericCellValue();
            setter.invoke(entity, Double.valueOf(numericCellValue).longValue());
        } else if (typeName.equals(Date.class.getTypeName())) {
            setter.invoke(entity, cell.getDateCellValue());
        } else if (typeName.equals(LocalDateTime.class.getTypeName())) {
            setter.invoke(entity, cell.getLocalDateTimeCellValue());
        } else if (typeName.equals(LocalDate.class.getTypeName())) {
            setter.invoke(entity, cell.getLocalDateTimeCellValue().toLocalDate());
        } else if (isCollectionOfExcelEntities(typeName, parametrizedType, parametrizedSignStart, parametrizedSignEnd)) {
            String value = cell.getStringCellValue();
            if (Strings.isNotBlank(value)) {
                List<ExcelIEEntity<?>> excelIEEntities = new ArrayList<>();
                String[] ids = value.split(",");
                Class<?> classExcelEntity = Class.forName(parametrizedType);
                ExcelIEEntity<?> excelEntityRef = initEntity(classExcelEntity);
                List<Method> setIdMethods = Arrays.stream(excelEntityRef.getClass().getDeclaredMethods())
                        .filter(e -> e.getName().equals("setId")).collect(Collectors.toList());
                for (String id : ids) {
                    setExcelEntityRefId(excelEntityRef, id.trim(), setIdMethods);
                    excelIEEntities.add(excelEntityRef);
                }

                if (typeName.replace(parametrizedType, "").equals(List.class.getTypeName()))
                    setter.invoke(entity, cell.getLocalDateTimeCellValue().toLocalDate());
            }
        }

//        if (value instanceof String) {
//            cell.setCellValue(value.toString());
//        } else if (value instanceof Boolean) {
//            cell.setCellValue(((Boolean) value));
//        } else if (value instanceof Date) {
//            cell.setCellValue(((Date) value));
//        } else if (value instanceof LocalDateTime) {
//            cell.setCellValue(((LocalDateTime) value));
//        } else if (value instanceof LocalDate) {
//            cell.setCellValue(((LocalDate) value));
//        } else if (value instanceof Double) {
//            cell.setCellValue(((Double) value));
//        } else if (value instanceof Long) {
//            cell.setCellValue(((Long) value));
//        } else if (value instanceof ExcelIEEntity) {
//            cell.setCellValue(String.valueOf(((ExcelIEEntity<?>) value).getId()));
//            exportExcel(Collections.singletonList(((ExcelIEEntity<?>) value)), workbook);
//        } else if (value instanceof Collection && ((Collection<?>) value).size() > 0) {
//            StringBuilder sb = new StringBuilder();
//            Iterator<?> iterator = ((Collection<?>) value).iterator();
//            Class<?> elementClass = null;
//            while (iterator.hasNext()) {
//
//                Object next = iterator.next();
//                if (elementClass == null) {
//                    elementClass = next.getClass();
//                }
//
//                if (elementClass == next.getClass() && next instanceof ExcelIEEntity) {
//                    sb.append(((ExcelIEEntity<?>) next).getId());
//                    sb.append(",");
//                } else {
//                    log.warn("Value is a non ExcelEntity collection. Will not be processed. Create custom exporter!");
//                    return;
//                }
//            }
//
//            if (sb.length() > 0) {
//                String idsSeparatedByComma = sb.toString();
//                idsSeparatedByComma = idsSeparatedByComma.substring(0, idsSeparatedByComma.length() - 1);
//                cell.setCellValue(idsSeparatedByComma);
//            }
//        }
    }

    protected void setExcelEntityRefId(ExcelIEEntity<?> excelEntityRef, String id, List<Method> setIdMethods) throws InvocationTargetException, IllegalAccessException {
        for (Method method : setIdMethods) {
            String typeName = method.getParameters()[0].getType().getName();
            if (typeName.equals(String.class.getTypeName())) {
                method.invoke(excelEntityRef, id);
                return;
            } else if (typeName.equals(Long.class.getTypeName())) {
                method.invoke(excelEntityRef, Long.parseLong(id));
                return;
            } else if (typeName.equals(Integer.class.getTypeName())) {
                method.invoke(excelEntityRef, Integer.parseInt(id));
                return;
            } else if (typeName.equals(UUID.class.getTypeName())) {
                method.invoke(excelEntityRef, UUID.fromString(id));
                return;
            } else {
                defaultMapper(excelEntityRef, id, setIdMethods, typeName);
            }
        }
        log.warn("Could not set excel entity ref id! Make sure setId method parameter is supported!");
        throw new RuntimeException("Could not set excel entity ref id!");
    }

    protected void defaultMapper(ExcelIEEntity<?> excelEntityRef, String id, List<Method> setIdMethods, String typeName) {

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
