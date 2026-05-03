package com.bervan.core.service;

import com.bervan.core.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@Slf4j
public class DTOMapper {
    private final List<? extends DefaultCustomMapper> defaultCustomMappers;
    private final List<? extends PreMapper> preMappers;
    private final List<? extends PostMapper> postMappers;

    public DTOMapper(List<? extends DefaultCustomMapper> defaultCustomMappers, List<? extends PreMapper> preMappers, List<? extends PostMapper> postMappers) {
        this.defaultCustomMappers = defaultCustomMappers;
        this.preMappers = preMappers;
        this.postMappers = postMappers;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            return new ObjectMapper()
                    .registerModule(new ParameterNamesModule())
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        } else {
            return objectMapper;
        }
    }

    private static boolean isFieldMapperConfigAnnotation(Object from, Object to, Field fromField, Field[] toFields) {
        if (from instanceof BaseDTO) {
            return fromField.getAnnotation(FieldMapperConfig.class) != null && !(fromField.getAnnotation(FieldMapperConfig.class).targetFieldNames().length == 0);
        }

        if (from instanceof BaseModel) {
            return Arrays.stream(toFields).filter(e -> e.getAnnotation(FieldMapperConfig.class) != null)
                    .anyMatch(e -> e.getAnnotation(FieldMapperConfig.class).targetFieldNames().length != 0);
        }

        return false;
    }

    public <ID> BaseDTO<ID> map(BaseModel<ID> dtoTarget, Class<? extends BaseDTO<ID>> dtoClass) throws Exception {
        BaseDTO<ID> dto = initDTO(dtoClass);
        preMappers.forEach(e -> {
            if (e.isApplicable(dtoTarget, dto)) {
                e.map(dtoTarget, dto);
            }
        });

        Field[] dtoFields = dtoClass.getDeclaredFields();
        for (Field fromField : dtoTarget.getClass().getDeclaredFields()) {
            processField(dtoTarget, dto, dtoFields, fromField);
        }

        for (Field dtoField : dtoFields) {
            FieldMapperConfig config = dtoField.getAnnotation(FieldMapperConfig.class);
            if (config == null) continue;
            for (String path : config.targetFieldNames()) {
                if (path.contains(".")) {
                    try {
                        Object nestedValue = getNestedValue(dtoTarget, path);
                        setValue(dto, dtoField, nestedValue);
                        break;
                    } catch (Exception e) {
                        log.warn("Could not map nested path '{}' from {}: {}", path, dtoTarget.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        }

        postMappers.forEach(e -> {
            if (e.isApplicable(dtoTarget, dto)) {
                e.map(dtoTarget, dto);
            }
        });

        return dto;
    }

    public <ID> BaseModel<ID> map(BaseDTO<ID> dto) throws Exception {
        Class<? extends BaseModel<ID>> dtoTargetClass = dto.dtoTarget();
        BaseModel<ID> targetDTO = initTargetDTO(dtoTargetClass);

        preMappers.forEach(e -> {
            if (e.isApplicable(dto, targetDTO)) {
                e.map(dto, targetDTO);
            }
        });

        Field[] dtoTargetFields = dtoTargetClass.getDeclaredFields();
        for (Field fromField : dto.getClass().getDeclaredFields()) {
            processField(dto, targetDTO, dtoTargetFields, fromField);
        }

        postMappers.forEach(e -> {
            if (e.isApplicable(dto, targetDTO)) {
                e.map(dto, targetDTO);
            }
        });

        return targetDTO;
    }

    private BaseDTO initDTO(Class<? extends BaseDTO> dtoClass) {
        try {
            return dtoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("DTO must have no args public constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private BaseModel initTargetDTO(Class<? extends BaseModel> dtoTargetClass) {
        try {
            return dtoTargetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Target DTO must have no args public constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private <ID> void processField(Object from, Object to, Field[] toFields, Field fromField) throws Exception {
        String name = fromField.getName();
        Class<?> fromFieldType = fromField.getType();
        List<Field> toFieldsWithTheSameName = getToFieldsWithTheSameName(toFields, name);

        if (toFieldsWithTheSameName.size() != 0) {

        } else {
            boolean hasFieldMapperConfigAnnotation = isFieldMapperConfigAnnotation(from, to, fromField, toFields);
            if (hasFieldMapperConfigAnnotation) {
                toFieldsWithTheSameName = getTargetFields(from, to, fromField, toFields);
            }
        }


        for (Field field : toFieldsWithTheSameName) {
            Field toField = field;
            Class<?> fieldType = toField.getType();
            Optional<? extends CustomMapper> customMapper = findCustomMapper(from, to, fromField, toField, fromFieldType, fieldType);
            Object value = receiveValue(from, fromField);
            if (shouldBeMappedWithCustomMapper(customMapper, value)) {
                value = customMapper.get().map(value, fromField, toField);
            } else if (shouldExecuteMap(from, fromField, toField)) {
                if (value instanceof Collection<?>) {
                    value = executeMapForCollection(value, toField);
                } else {
                    value = executeMapForSingleObject(value, toField);
                }
            }
            setOrAddValues(to, toField, value);
        }
    }


    private void setOrAddValues(Object to, Field toField, Object value) throws IllegalAccessException {
        toField.setAccessible(true);
        Object o = toField.get(to);
        if (o instanceof Collection) {
            if (value instanceof Collection) {
                ((Collection) o).addAll((Collection) value);
            } else {
                ((Collection) o).add(value);
            }
        } else {
            toField.set(to, value);
        }
    }

    private <ID> Object executeMapForSingleObject(Object value, Field toField) throws Exception {
        if (value instanceof BaseDTO) {
            value = map((BaseDTO<?>) value);
        } else {
            value = map(
                    (BaseModel) value,
                    (Class<? extends BaseDTO<ID>>) toField.getType()
            );
        }
        return value;
    }

    private <ID> Object executeMapForCollection(Object value, Field toField) throws Exception {
        Collection<?> sourceCollection = (Collection<?>) value;

        Collection<Object> targetCollection =
                value instanceof Set ? new HashSet<>() : new ArrayList<>();

        Class<?> targetElementType = getFieldType(toField);

        for (Object element : sourceCollection) {
            if (element == null) continue;

            Object mappedElement;

            if (element instanceof BaseDTO) {
                mappedElement = map((BaseDTO<?>) element);
            } else {
                mappedElement = map(
                        (BaseModel) element,
                        (Class<? extends BaseDTO<ID>>) targetElementType
                );
            }

            targetCollection.add(mappedElement);
        }

        value = targetCollection;
        return value;
    }

    // Read value using dot path like "project.name"
    private Object getNestedValue(Object source, String path) throws IllegalAccessException {
        String[] parts = path.split("\\.");
        Object current = source;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            Field field = Arrays.stream(current.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals(part))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Field not found: " + part));

            field.setAccessible(true);
            current = field.get(current);
            field.setAccessible(false);
        }

        return current;
    }

    private List<Field> getTargetFields(Object from, Object to, Field fromField, Field[] toFields) {
        if (from instanceof BaseDTO) {
            String[] names = fromField.getAnnotation(FieldMapperConfig.class).targetFieldNames();

            List<Field> toFieldWithTheSameName = new ArrayList<>();
            for (String supportedName : names) {
                toFieldWithTheSameName = getToFieldsWithTheSameName(toFields, supportedName);
                if (toFieldWithTheSameName.size() != 0) {
                    break;
                }
            }
            return toFieldWithTheSameName;
        } else {
            return Arrays.stream(toFields).filter(e -> e.getAnnotation(FieldMapperConfig.class) != null)
                    .filter(e -> Arrays.stream(e.getAnnotation(FieldMapperConfig.class).targetFieldNames()).anyMatch(e1 -> e1.equals(fromField.getName())))
                    .toList();
        }
    }

    private boolean shouldBeMappedWithCustomMapper(Optional<? extends CustomMapper> customMapper, Object value) {
        return customMapper.isPresent() && value != null;
    }

    private List<Field> getToFieldsWithTheSameName(Field[] dtoFields, String name) {
        return Arrays.stream(dtoFields).filter(e -> e.getName().equals(name)).toList();
    }

    /**
     * @param fromObj   Object that map() was executed for
     * @param fromField Field in the fromObj
     * @param toField   Field in class related with fromObj
     *                  Method checks whether the fromField should be mapped using map()
     *                  with recursion to DTO or TargetDTO class
     * @return true/false
     * @throws IllegalAccessException
     */
    private boolean shouldExecuteMap(Object fromObj, Field fromField, Field toField)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Class<?> fromType = getFieldType(fromField);
        Class<?> toType = getFieldType(toField);

        if (BaseModel.class.isAssignableFrom(fromType) && BaseDTO.class.isAssignableFrom(toType)) {
            return shouldBeMappedToDTO(fromObj, fromField, toField, fromType, toType);
        } else if (BaseDTO.class.isAssignableFrom(fromType) && BaseModel.class.isAssignableFrom(toType)) {
            return shouldBeMappedToDTOTarget(fromObj, fromField, toField, fromType, toType);
        }

        return false;
    }

    private boolean shouldBeMappedToDTOTarget(Object fromObj, Field fromField, Field toField,
                                              Class<?> fromType, Class<?> toType)
            throws IllegalAccessException {

        fromField.setAccessible(true);
        Object value = fromField.get(fromObj);
        fromField.setAccessible(false);
        if (value == null) return false;
        BaseDTO dto = (BaseDTO) value;
        return toType.equals(dto.dtoTarget());
    }

    private Class<?> getFieldType(Field field) {
        // If it's a collection, extract generic type
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            return (Class<?>) type.getActualTypeArguments()[0];
        }

        return field.getType();
    }

    private boolean shouldBeMappedToDTO(Object fromObj, Field fromField, Field toField,
                                        Class<?> fromType, Class<?> toType)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        fromField.setAccessible(true);
        Object value = fromField.get(fromObj);
        fromField.setAccessible(false);
        if (value == null) return false;
        BaseDTO dtoInstance = (BaseDTO) toType.getConstructor().newInstance();
        return dtoInstance.dtoTarget().equals(fromType);
    }

    private void setValue(Object to, Field toField, Object value) throws IllegalAccessException {
        toField.setAccessible(true);
        toField.set(to, value);
        toField.setAccessible(false);
    }

    private Object receiveValue(Object from, Field fromField) throws IllegalAccessException {
        return simpleReceivingValue(from, fromField);
    }


    private Object simpleReceivingValue(Object from, Field declaredField) throws IllegalAccessException {
        declaredField.setAccessible(true);
        Object value = declaredField.get(from);
        declaredField.setAccessible(false);
        return value;
    }

    /**
     * @param fromObj
     * @param toObj
     * @param fromField Field in FROM object
     * @param toField
     * @param from      FROM field class
     * @param to        TO field class
     *                  Method first tries to find @FieldCustomMapper on the fromField if not found then checks whether
     *                  in the application was created any CustomMapper that can be user for mapping FROM to TO.
     * @return custom mapper if found
     */
    private Optional<? extends CustomMapper> findCustomMapper(Object fromObj, Object toObj, Field fromField, Field toField, Class<?> from, Class<?> to) {
        Field field = fromField;
        if (field.getAnnotation(FieldMapperConfig.class) != null) {
            if (field.getAnnotation(FieldMapperConfig.class).mapper() != null) {
                Optional<? extends DefaultCustomMapper> fieldCustomMapperWithSpringContext = defaultCustomMappers.stream()
                        .filter(e -> e.getFrom().equals(from) && e.getTo().equals(to))
                        .filter(e -> e.getClass().equals(field.getAnnotation(FieldMapperConfig.class).mapper())).findFirst();

                if (fieldCustomMapperWithSpringContext.isPresent()) {
                    return Optional.of(fieldCustomMapperWithSpringContext.get());
                }

                try {
                    Class<? extends CustomMapper> mapper = field.getAnnotation(FieldMapperConfig.class).mapper();
                    if (mapper.getName() == CustomMapper.class.getName()) {
                        return Optional.empty();
                    }
                    CustomMapper value = mapper
                            .getDeclaredConstructor()
                            .newInstance();
                    return Optional.of(value);
                } catch (Exception e) {
                    log.error("CustomMapper must have no args public constructor!", e);
                    throw new RuntimeException(e);
                }
            }
        }

        return defaultCustomMappers.stream().filter(e -> e.getFrom().equals(from) && e.getTo().equals(to)).findFirst();
    }

    private static final ObjectMapper objectMapper = getObjectMapper();


}
