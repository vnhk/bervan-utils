package com.bervan.core.model;

import java.lang.reflect.Field;

public interface CustomMapper<FROM, TO> {
    TO map(FROM obj, Field fromField, Field toField);
}
