package com.bervan.dtocore.service;

import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
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
    private final List<? extends CustomMapper> customMappers;
    private static final ObjectMapper objectMapper = getObjectMapper();

    public DTOMapper(List<? extends CustomMapper> customMappers) {
        this.customMappers = customMappers;
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
            Optional<? extends CustomMapper> customMapper = findCustomMapper(dtoTargetFieldType, dtoFieldType);
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

    private Optional<? extends CustomMapper> findCustomMapper(Class<?> from, Class<?> to) {
        //add possibility to create custom mapper for given use case instead of using global custom mapper, maybe by
        //using some @CustomMapper(class="CUSTOM_MAPPER_CLASS") and check if field is annotated if not find global custom mapper
        return customMappers.stream().filter(e -> e.getFrom().equals(from) && e.getTo().equals(to)).findFirst();
    }

    public <ID> BaseDTOTarget<ID> map(BaseDTO<ID> dto) throws JsonProcessingException {
        return null;
    }

}
