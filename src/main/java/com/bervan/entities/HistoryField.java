package com.bervan.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryField {
    String savePath() default "";
    String comparePath() default "";
    boolean comparable() default true;

    boolean isTargetEntity() default false;
}
