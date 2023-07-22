package com.bervan.core.service;

import com.bervan.core.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DTOMapper {
    private final List<? extends DefaultCustomMapper> defaultCustomMappers;
    private static final ObjectMapper objectMapper = getObjectMapper();

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

//    public <ID> BaseDTO<ID> map(BaseDTOTarget<ID> dtoTarget) throws JsonProcessingException {
//        String dtoTargetAsString = objectMapper.writeValueAsString(dtoTarget);
//        return objectMapper.readValue(dtoTargetAsString, dtoTarget.dto());
//    }
//
//    public <ID> BaseDTOTarget<ID> map(BaseDTO<ID> dto) throws JsonProcessingException {
//        String dtoAsString = objectMapper.writeValueAsString(dto);
//        return objectMapper.readValue(dtoAsString, dto.dtoTarget());
//    }

    public <ID> BaseDTO<ID> map(BaseDTOTarget<ID> dtoTarget) throws Exception {
        Class<? extends BaseDTO<ID>> dtoClass = dtoTarget.dto();
        BaseDTO<ID> dto = initDTO(dtoClass);

        Field[] dtoFields = dtoClass.getDeclaredFields();
        for (Field fromField : dtoTarget.getClass().getDeclaredFields()) {
            processField(dtoTarget, dto, dtoFields, fromField);
        }

        return dto;
    }

    public <ID> BaseDTOTarget<ID> map(BaseDTO<ID> dto) throws Exception {
        Class<? extends BaseDTOTarget<ID>> dtoTargetClass = dto.dtoTarget();
        BaseDTOTarget<ID> targetDTO = initTargetDTO(dtoTargetClass);

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

    private BaseDTOTarget initTargetDTO(Class<? extends BaseDTOTarget> dtoTargetClass) {
        try {
            return dtoTargetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Target DTO must have no args public constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private void processField(Object from, Object to, Field[] toFields, Field fromField) throws Exception {
        String name = fromField.getName();
        Class<?> fromFieldType = fromField.getType();
        Optional<Field> toFieldWithTheSameName = getToFieldWithTheSameName(toFields, name);

        if (toFieldWithTheSameName.isPresent()) {
            Field toField = toFieldWithTheSameName.get();
            Class<?> dtoFieldType = toField.getType();
            Optional<? extends CustomMapper> customMapper = findCustomMapper(fromField, fromFieldType, dtoFieldType);
            Object value = simpleReceivingValue(from, fromField);
            if (shouldBeMappedWithCustomMapper(customMapper, value)) {
                value = customMapper.get().map(value);
            } else if (shouldExecuteMap(from, fromField, toField)) {
                if (value instanceof BaseDTO) {
                    value = map(((BaseDTO<?>) value));
                } else {
                    value = map(((BaseDTOTarget) value));
                }
            }

            setValue(to, toField, value);
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
    private boolean shouldExecuteMap(Object fromObj, Field fromField, Field toField) throws IllegalAccessException {
        if (BaseDTOTarget.class.isAssignableFrom(fromField.getType()) && BaseDTO.class.isAssignableFrom(toField.getType())) {
            return shouldBeMappedToDTO(fromObj, fromField, toField);
        } else if (BaseDTO.class.isAssignableFrom(fromField.getType()) && BaseDTOTarget.class.isAssignableFrom(toField.getType())) {
            return shouldBeMappedToDTOTarget(fromObj, fromField, toField);
        }

        return false;
    }

    private boolean shouldBeMappedToDTO(Object fromObj, Field fromField, Field toField) throws IllegalAccessException {
        fromField.setAccessible(true);
        BaseDTOTarget from = ((BaseDTOTarget) fromField.get(fromObj));
        fromField.setAccessible(false);

        return from != null && toField.getAnnotatedType().getType().equals(from.dto());
    }


    private boolean shouldBeMappedToDTOTarget(Object fromObj, Field fromField, Field toField) throws IllegalAccessException {
        fromField.setAccessible(true);
        BaseDTO from = ((BaseDTO) fromField.get(fromObj));
        fromField.setAccessible(false);

        return from != null && toField.getAnnotatedType().getType().equals(from.dtoTarget());
    }

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
     * @param fromField Field in FROM object
     * @param from      FROM field class
     * @param to        TO field class
     *                  Method first tries to find @FieldCustomMapper on the fromField if not found then checks whether
     *                  in the application was created any CustomMapper that can be user for mapping FROM to TO.
     * @return custom mapper if found
     */
    private Optional<? extends CustomMapper> findCustomMapper(Field fromField, Class<?> from, Class<?> to) {
        if (fromField.getAnnotation(FieldCustomMapper.class) != null) {
            try {
                CustomMapper value = fromField.getAnnotation(FieldCustomMapper.class).mapper()
                        .getDeclaredConstructor()
                        .newInstance();
                return Optional.of(value);
            } catch (Exception e) {
                log.error("CustomMapper must have no args public constructor!", e);
                throw new RuntimeException(e);
            }
        }

        return defaultCustomMappers.stream().filter(e -> e.getFrom().equals(from) && e.getTo().equals(to)).findFirst();
    }
}
