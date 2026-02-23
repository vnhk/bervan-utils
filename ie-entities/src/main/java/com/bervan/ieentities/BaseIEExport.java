package com.bervan.ieentities;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseIEExport {
    private final Map<Class<? extends ExcelIEEntity<?>>, List<Object>> processedEntities = new HashMap<>();
    @Setter
    protected List<String> columnsToExport = new ArrayList<>();

    protected boolean isAlreadyProcessed(ExcelIEEntity<?> entity) {
        List<Object> ids = processedEntities.get(entity.getClass());
        return ids != null && ids.contains(entity.getId());
    }

    protected void appendProcessedEntity(ExcelIEEntity<?> entity) {
        List<Object> processedIds = processedEntities.get(entity.getClass());
        if (processedIds == null) {
            processedIds = new ArrayList<>();
        }
        processedIds.add(entity.getId());
        processedEntities.put((Class<? extends ExcelIEEntity<?>>) entity.getClass(), processedIds);
    }

    protected void clearProcessed() {
        processedEntities.clear();
    }

    protected List<Method> getGettersToExportData(Object object) {
        Set<String> fields = Arrays.stream(object.getClass().getDeclaredFields())
                .map(Field::getName).map(String::toLowerCase).collect(Collectors.toSet());

        if (columnsToExport != null && !columnsToExport.isEmpty()) {
            columnsToExport = columnsToExport.stream().map(String::toLowerCase).collect(Collectors.toList());
            fields = fields.stream().filter(e -> columnsToExport.contains(e)).collect(Collectors.toSet());
        }

        Set<String> finalFields = fields;
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> !e.getName().equals("getId"))
                .filter(e -> e.getName().startsWith("get"))
                .filter(e -> finalFields.contains(e.getName().replace("get", "").toLowerCase()))
                .collect(Collectors.toList());
    }

    protected Object getVal(ExcelIEEntity<?> entity, Method getter) throws Exception {
        return getter.invoke(entity);
    }
}
