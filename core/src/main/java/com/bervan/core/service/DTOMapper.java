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

    public DTOMapper(List<? extends DefaultCustomMapper> defaultCustomMappers) {
        this.defaultCustomMappers = defaultCustomMappers;
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

        Field[] dtoFields = dtoClass.getDeclaredFields();
        for (Field fromField : dtoTarget.getClass().getDeclaredFields()) {
            processField(dtoTarget, dto, dtoFields, fromField);
        }

        return dto;
    }

    public <ID> BaseModel<ID> map(BaseDTO<ID> dto) throws Exception {
        Class<? extends BaseModel<ID>> dtoTargetClass = dto.dtoTarget();
        BaseModel<ID> targetDTO = initTargetDTO(dtoTargetClass);

        Field[] dtoTargetFields = dtoTargetClass.getDeclaredFields();
        for (Field fromField : dto.getClass().getDeclaredFields()) {
            processField(dto, targetDTO, dtoTargetFields, fromField);
        }

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
        Optional<Field> toFieldWithTheSameName = getToFieldWithTheSameName(toFields, name);

        if (toFieldWithTheSameName.isPresent()) {

        } else {
            boolean hasFieldMapperConfigAnnotation = isFieldMapperConfigAnnotation(from, to, fromField, toFields);
            if (hasFieldMapperConfigAnnotation) {
                toFieldWithTheSameName = getTargetField(from, to, fromField, toFields);
            }
        }

        if (toFieldWithTheSameName.isPresent()) {
            Field toField = toFieldWithTheSameName.get();
            Class<?> fieldType = toField.getType();
            Optional<? extends CustomMapper> customMapper = findCustomMapper(from, to, fromField, toField, fromFieldType, fieldType);
            Object value = simpleReceivingValue(from, fromField);
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

    private Optional<Field> getTargetField(Object from, Object to, Field fromField, Field[] toFields) {
        if (from instanceof BaseDTO) {
            String[] names = fromField.getAnnotation(FieldMapperConfig.class).targetFieldNames();

            Optional<Field> toFieldWithTheSameName = Optional.empty();
            for (String supportedName : names) {
                toFieldWithTheSameName = getToFieldWithTheSameName(toFields, supportedName);
                if (toFieldWithTheSameName.isPresent()) {
                    break;
                }
            }
            return toFieldWithTheSameName;
        } else {
            return Arrays.stream(toFields).filter(e -> e.getAnnotation(FieldMapperConfig.class) != null)
                    .filter(e -> Arrays.stream(e.getAnnotation(FieldMapperConfig.class).targetFieldNames()).anyMatch(e1 -> e1.equals(fromField.getName())))
                    .findFirst();
        }
    }

    private boolean shouldBeMappedWithCustomMapper(Optional<? extends CustomMapper> customMapper, Object value) {
        return customMapper.isPresent() && value != null;
    }

    private Optional<Field> getToFieldWithTheSameName(Field[] dtoFields, String name) {
        return Arrays.stream(dtoFields).filter(e -> e.getName().equals(name)).findFirst();
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
    }    private static final ObjectMapper objectMapper = getObjectMapper();

    private void setValue(Object to, Field toField, Object value) throws IllegalAccessException {
        toField.setAccessible(true);
        toField.set(to, value);
        toField.setAccessible(false);
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
        Field field;
        if (fromObj instanceof BaseDTO) {
            field = fromField;
        } else {
            field = toField;
        }
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




}
