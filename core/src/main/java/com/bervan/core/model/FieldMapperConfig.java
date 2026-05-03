package com.bervan.core.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapperConfig {
    Class<? extends CustomMapper> mapper() default CustomMapper.class;

    String[] targetFieldNames() default {};
}
