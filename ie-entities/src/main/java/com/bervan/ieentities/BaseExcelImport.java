package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Slf4j
public class BaseExcelImport {
    private final Map<Class<? extends ExcelIEEntity<?>>, List<Object>> processedEntities = new HashMap<>();
    private final Map<Class<?>, Sheet> sheets = new HashMap<>();
    private final List<? extends ExcelIEEntity<?>> entities = new ArrayList<>();
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

    public List<? extends ExcelIEEntity<?>> importExcel(Workbook workbook) {
        loadSheets(workbook);

        for (Map.Entry<Class<?>, Sheet> classSheetEntry : sheets.entrySet()) {
            importData(classSheetEntry.getKey(), classSheetEntry.getValue());
        }

        return entities;
    }

    private void importData(Class<?> entity, Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        List<String> headerNames = getHeaderNames(sheet);

        for (int i = 1; i < lastRowNum; i++) {
        }
    }

    private List<String> getHeaderNames(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = headerRow.getLastCellNum();
        List<String> headersNames = new ArrayList<>();
        for (int i = 0; i < lastCellNum; i++) {
            headersNames.add(headerRow.getCell(i).getStringCellValue());
        }

        return headersNames;
    }

    private ExcelIEEntity<?> initEntity(Class<?> cl) {
        try {
            return (ExcelIEEntity<?>) cl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Entity must have no args constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private void loadSheets(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Optional<Class<?>> classToImport = classesSupportsImport.stream().filter(cl -> cl.getSimpleName().equals(sheet.getSheetName()))
                    .findFirst();

            if (classToImport.isPresent()) {
                sheets.put(classToImport.get().getClass(), sheet);
            } else {
                log.warn("Could not find available class for class name:" + sheet.getSheetName() + ". Skipping.");
            }
        }
    }
}
