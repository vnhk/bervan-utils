package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseIEImport {
    protected final List entities = new ArrayList<>();
    protected final List<Class<?>> classesSupportsImport;

    public BaseIEImport(List<Class<?>> classesSupportsImport) {
        this.classesSupportsImport = classesSupportsImport;
    }

    protected ExcelIEEntity<?> initEntity(Class<?> cl) {
        try {
            return (ExcelIEEntity<?>) cl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Entity must have no args constructor!", e);
        }
    }

    protected List<Field> getFieldsToImportData(Class<?> entity, List<String> headersName) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(e -> !e.isAnnotationPresent(ExcelIgnore.class))
                .filter(e -> headersName.contains(e.getName().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    protected void setExcelEntityRefId(ExcelIEEntity<?> excelEntityRef, String id, Field idField)
            throws InvocationTargetException, IllegalAccessException {
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

        throw new RuntimeException("Could not set entity ref id!");
    }

    protected void defaultMapper(ExcelIEEntity<?> excelEntityRef, String id, Field idField, String typeName) {
    }

    protected boolean isCollectionOfExcelEntities(String typeName,
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
