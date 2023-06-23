package com.bervan.dtocore.service;

import com.bervan.dtocore.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        //add better exception handling with logs...
        BaseDTO<ID> dto = initDTO(dtoClass);

        Field[] dtoFields = dtoClass.getDeclaredFields();
        for (Field fromField : dtoTarget.getClass().getDeclaredFields()) {
            processField(dtoTarget, dto, dtoFields, fromField);
        }

        return dto;
    }

    private BaseDTO initDTO(Class<? extends BaseDTO> dtoClass) {
        try {
            return dtoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("DTO must have no args public constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private <ID> void processField(BaseDTOTarget<ID> dtoTarget, BaseDTO<ID> dto, Field[] dtoFields, Field fromField) throws Exception {
        String name = fromField.getName();
        Class<?> dtoTargetFieldType = fromField.getType();
        Optional<Field> dtoFieldWithTheSameName = getDtoFieldWithTheSameName(dtoFields, name);

        if (dtoFieldWithTheSameName.isPresent()) {
            Field toField = dtoFieldWithTheSameName.get();
            Class<?> dtoFieldType = toField.getType();
            Optional<? extends CustomMapper> customMapper = findCustomMapper(fromField, dtoTargetFieldType, dtoFieldType);
            Object value = simpleReceivingValue(dtoTarget, fromField);
            if (shouldBeMappedWithCustomMapper(customMapper, value)) {
                value = customMapper.get().map(value);
            } else if (shouldBeMappedToDTO(dtoTarget, fromField, toField)) {
                value = map((BaseDTOTarget) value);
            }

            setValue(dto, toField, value);
        }
    }

    private boolean shouldBeMappedWithCustomMapper(Optional<? extends CustomMapper> customMapper, Object value) {
        return customMapper.isPresent() && value != null;
    }

    private Optional<Field> getDtoFieldWithTheSameName(Field[] dtoFields, String name) {
        return Arrays.stream(dtoFields).filter(e -> e.getName().equals(name)).findFirst();
    }

    /**
     * @param fromBaseObject BaseDTOTarget object that map() was executed for
     * @param fromField      Field in BaseDTOTarget object
     * @param toField        Field in DTO class related with BaseDTOTargetObject
     *                       Method checks whether the fromField is not null BaseDTOTarget related with toField DTO type in order to run map()
     *                       that will map fromField to toField
     * @return true/false
     * @throws IllegalAccessException
     */
    private boolean shouldBeMappedToDTO(BaseDTOTarget fromBaseObject, Field fromField, Field toField) throws IllegalAccessException {
        if (BaseDTOTarget.class.isAssignableFrom(fromField.getType())
                && BaseDTO.class.isAssignableFrom(toField.getType())) {
            fromField.setAccessible(true);
            BaseDTOTarget from = ((BaseDTOTarget) fromField.get(fromBaseObject));
            fromField.setAccessible(false);

            return from != null && toField.getAnnotatedType().getType().equals(from.dto());
        }

        return false;
    }

    private <ID> void setValue(BaseDTO<ID> dto, Field toField, Object value) throws IllegalAccessException {
        toField.setAccessible(true);
        toField.set(dto, value);
        toField.setAccessible(false);
    }

    private <ID> Object simpleReceivingValue(BaseDTOTarget<ID> dtoTarget, Field declaredField) throws IllegalAccessException {
        declaredField.setAccessible(true);
        Object value = declaredField.get(dtoTarget);
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

    public <ID> BaseDTOTarget<ID> map(BaseDTO<ID> dto) throws JsonProcessingException {
        return null;
    }

}
