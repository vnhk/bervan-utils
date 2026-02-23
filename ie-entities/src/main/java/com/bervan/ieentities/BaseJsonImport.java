package com.bervan.ieentities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseJsonImport extends BaseIEImport {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BaseJsonImport(List<Class<?>> classesSupportsImport) {
        super(classesSupportsImport);
    }

    public Map<String, List<Map<String, Object>>> load(String dirPath, String fileName) {
        if (dirPath == null || dirPath.isBlank()) {
            dirPath = ".";
        }
        if (fileName == null || fileName.isBlank()) {
            fileName = "temp";
        }

        File currDir = new File(dirPath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + File.separator + fileName;

        try (FileInputStream inputStream = new FileInputStream(fileLocation)) {
            return readJsonData(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load JSON!", e);
        }
    }

    public Map<String, List<Map<String, Object>>> load(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return readJsonData(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load JSON!", e);
        }
    }

    public Map<String, List<Map<String, Object>>> load(InputStream inputStream) {
        try {
            return readJsonData(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load JSON!", e);
        }
    }

    private Map<String, List<Map<String, Object>>> readJsonData(InputStream inputStream) throws Exception {
        return objectMapper.readValue(inputStream, new TypeReference<>() {});
    }

    public List<?> importJson(Map<String, List<Map<String, Object>>> jsonData) {
        entities.clear();
        try {
            for (Map.Entry<String, List<Map<String, Object>>> entry : jsonData.entrySet()) {
                String className = entry.getKey();
                Optional<Class<?>> classToImport = classesSupportsImport.stream()
                        .filter(cl -> cl.getSimpleName().equals(className))
                        .findFirst();

                if (classToImport.isPresent()) {
                    log.info("Importing " + className + "...");
                    log.info("Records to be imported: " + entry.getValue().size());
                    importData(classToImport.get(), entry.getValue());
                } else {
                    log.warn("Could not find available class for: " + className + ". Skipping.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Import failed!", e);
        }
        return entities;
    }

    protected void importData(Class<?> entityClass, List<Map<String, Object>> records) throws Exception {
        for (Map<String, Object> record : records) {
            ExcelIEEntity<?> entity = initEntity(entityClass);

            List<String> headerNames = record.keySet().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            List<Field> fieldsToImport = getFieldsToImportData(entityClass, headerNames);

            for (Field field : fieldsToImport) {
                String matchingKey = record.keySet().stream()
                        .filter(k -> k.equalsIgnoreCase(field.getName()))
                        .findFirst()
                        .orElse(null);

                if (matchingKey != null) {
                    Object rawValue = record.get(matchingKey);
                    if (rawValue != null) {
                        setValue(entity, field, rawValue);
                    }
                }
            }

            entities.add(entity);
        }
    }

    protected void setValue(ExcelIEEntity<?> entity, Field field, Object rawValue) throws Exception {
        String typeName = field.getType().getTypeName();
        String parametrizedType = null;
        int parametrizedSignStart = 0;
        int parametrizedSignEnd = 0;
        if (typeName.contains("<") && typeName.contains(">")) {
            parametrizedSignStart = typeName.indexOf("<") + 1;
            parametrizedSignEnd = typeName.indexOf(">");
            parametrizedType = typeName.substring(parametrizedSignStart, parametrizedSignEnd);
        }

        String strValue = rawValue.toString();
        field.setAccessible(true);

        if (typeName.equals(String.class.getTypeName())) {
            field.set(entity, strValue);
        } else if (typeName.equals(Boolean.class.getTypeName())) {
            if (rawValue instanceof Boolean) {
                field.set(entity, rawValue);
            } else {
                field.set(entity, Boolean.parseBoolean(strValue));
            }
        } else if (Enum.class.isAssignableFrom(field.getType())) {
            Class<? extends Enum> type = (Class<? extends Enum>) field.getType();
            field.set(entity, Enum.valueOf(type, strValue));
        } else if (typeName.equals(Double.class.getTypeName())) {
            if (rawValue instanceof Number) {
                field.set(entity, ((Number) rawValue).doubleValue());
            } else {
                field.set(entity, Double.parseDouble(strValue));
            }
        } else if (typeName.equals(BigDecimal.class.getTypeName())) {
            if (rawValue instanceof Number) {
                field.set(entity, BigDecimal.valueOf(((Number) rawValue).doubleValue()));
            } else {
                field.set(entity, new BigDecimal(strValue));
            }
        } else if (typeName.equals(Integer.class.getTypeName())) {
            if (rawValue instanceof Number) {
                field.set(entity, ((Number) rawValue).intValue());
            } else {
                field.set(entity, Integer.parseInt(strValue));
            }
        } else if (typeName.equals(Long.class.getTypeName())) {
            if (rawValue instanceof Number) {
                field.set(entity, ((Number) rawValue).longValue());
            } else {
                field.set(entity, Long.parseLong(strValue));
            }
        } else if (typeName.equals(UUID.class.getTypeName())) {
            field.set(entity, UUID.fromString(strValue));
        } else if (typeName.equals(Date.class.getTypeName())) {
            field.set(entity, Date.from(Instant.parse(strValue)));
        } else if (typeName.equals(LocalDateTime.class.getTypeName())) {
            field.set(entity, LocalDateTime.parse(strValue));
        } else if (typeName.equals(LocalDate.class.getTypeName())) {
            field.set(entity, LocalDate.parse(strValue));
        } else if (isCollectionOfExcelEntities(typeName, parametrizedType, parametrizedSignStart, parametrizedSignEnd)) {
            if (!strValue.isBlank()) {
                List<ExcelIEEntity<?>> excelIEEntities = new ArrayList<>();
                String[] ids = strValue.split(",");
                Class<?> classExcelEntity = Class.forName(parametrizedType);
                for (String id : ids) {
                    ExcelIEEntity<?> excelEntityRef = initEntity(classExcelEntity);
                    Field idField = Arrays.stream(excelEntityRef.getClass().getDeclaredFields())
                            .filter(f -> f.getName().equalsIgnoreCase("Id")).findFirst().get();
                    setExcelEntityRefId(excelEntityRef, id.trim(), idField);
                    excelIEEntities.add(excelEntityRef);
                }
                field.set(entity, excelIEEntities);
            }
        } else if (ExcelIEEntity.class.isAssignableFrom(field.getType())) {
            Class<?> classExcelEntity = Class.forName(typeName);
            ExcelIEEntity<?> excelEntityRef = initEntity(classExcelEntity);
            Field idField = Arrays.stream(excelEntityRef.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equalsIgnoreCase("Id")).findFirst().get();
            setExcelEntityRefId(excelEntityRef, strValue.trim(), idField);
            field.set(entity, excelEntityRef);
        }

        field.setAccessible(false);
    }
}
