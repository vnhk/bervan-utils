package com.bervan.dtocore.service;

import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
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
        BaseDTO<ID> dto = dtoClass.getDeclaredConstructor().newInstance();

        Field[] dtoFields = dtoClass.getDeclaredFields();
        for (Field declaredField : dtoTarget.getClass().getDeclaredFields()) {
            String name = declaredField.getName();
            Class<?> dtoTargetFieldType = declaredField.getType();
            Optional<Field> dtoFieldWithTheSameName =
                    Arrays.stream(dtoFields).filter(e -> e.getName().equals(name)).findFirst();

            if (dtoFieldWithTheSameName.isPresent()) {
                Field field = dtoFieldWithTheSameName.get();
                Class<?> dtoFieldType = field.getType();
                Optional<? extends CustomMapper> customMapper = findCustomMapper(dtoTargetFieldType, dtoFieldType);
                Object value = null;
                if (customMapper.isPresent()) {
                    value = customMapper.get().map(dtoTarget, declaredField);
                } else {
                    declaredField.setAccessible(true);
                    value = declaredField.get(dtoTarget);
                    declaredField.setAccessible(false);
                }
                field.setAccessible(true);
                declaredField.set(dto, value);
                field.setAccessible(false);
            }
        }

        return dto;
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
