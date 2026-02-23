package com.bervan.ieentities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class BaseJsonExport extends BaseIEExport {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private Map<String, List<Map<String, Object>>> data;

    public Map<String, List<Map<String, Object>>> exportJson(List<? extends ExcelIEEntity<?>> entities) {
        log.info("Exporting " + entities.size() + " entities to JSON.");
        this.data = new LinkedHashMap<>();
        clearProcessed();
        processEntities(entities);
        return this.data;
    }

    public File save(Map<String, List<Map<String, Object>>> data, String dirPath, String fileName) {
        if (dirPath == null || dirPath.isBlank()) {
            dirPath = ".";
        }
        if (fileName == null || fileName.isBlank()) {
            fileName = "temp";
        }

        File currDir = new File(dirPath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + File.separator + fileName;
        File toSave = new File(fileLocation);

        try (FileOutputStream outputStream = new FileOutputStream(toSave)) {
            objectMapper.writeValue(outputStream, data);
        } catch (Exception e) {
            log.error("Cannot save JSON!", e);
            throw new RuntimeException("Cannot save JSON!", e);
        }

        return toSave;
    }

    private void processEntities(List<? extends ExcelIEEntity<?>> entities) {
        for (ExcelIEEntity<?> entity : entities) {
            if (entity.getId() == null) {
                log.error("Could not export entities with id = null!");
                throw new RuntimeException("Could not export entities with id = null!");
            }

            if (isAlreadyProcessed(entity)) {
                log.info("Entity " + entity.getClass().getSimpleName() + " with id = " + entity.getId() + " already exported. Skip.");
                continue;
            }

            appendProcessedEntity(entity);
            processEntity(entity);
        }
    }

    protected void processEntity(ExcelIEEntity<?> entity) {
        String entityName = entity.getClass().getSimpleName();
        List<Method> getters = getGettersToExportData(entity);

        List<Map<String, Object>> entityList = data.computeIfAbsent(entityName, k -> new ArrayList<>());
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("Id", entity.getId().toString());

        for (Method getter : getters) {
            String fieldName = getter.getName().split("get")[1];
            try {
                Object val = getVal(entity, getter);
                Object converted = convertValue(val);
                if (converted != null) {
                    record.put(fieldName, converted);
                }
            } catch (Exception e) {
                log.error("Could not export " + fieldName + "!", e);
                throw new RuntimeException("Could not export " + fieldName + "!", e);
            }
        }

        entityList.add(record);
    }

    protected Object convertValue(Object value) {
        if (value == null) return null;
        if (value instanceof String) return value;
        if (value instanceof Boolean) return value;
        if (value instanceof Number) return value;
        if (value instanceof Enum) return ((Enum<?>) value).name();
        if (value instanceof Date) return ((Date) value).toInstant().toString();
        if (value instanceof LocalDateTime) return value.toString();
        if (value instanceof LocalDate) return value.toString();
        if (value instanceof BigDecimal) return value.toString();

        if (value instanceof ExcelIEEntity) {
            ExcelIEEntity<?> ref = (ExcelIEEntity<?>) value;
            processEntities(Collections.singletonList(ref));
            return String.valueOf(ref.getId());
        }

        if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            if (coll.isEmpty()) return null;
            List<String> ids = new ArrayList<>();
            for (Object item : coll) {
                if (item instanceof ExcelIEEntity) {
                    ids.add(String.valueOf(((ExcelIEEntity<?>) item).getId()));
                } else {
                    return null;
                }
            }
            return String.join(",", ids);
        }

        return null;
    }
}
