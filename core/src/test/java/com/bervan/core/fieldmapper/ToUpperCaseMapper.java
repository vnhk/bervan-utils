package com.bervan.core.fieldmapper;

import com.bervan.core.model.CustomMapper;

import java.lang.reflect.Field;

public class ToUpperCaseMapper implements CustomMapper<String, String> {
    @Override
    public String map(String obj, Field fromField, Field toField) {
        return obj.toUpperCase();
    }
}
