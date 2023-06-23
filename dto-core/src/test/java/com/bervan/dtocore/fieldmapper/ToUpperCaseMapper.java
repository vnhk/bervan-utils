package com.bervan.dtocore.fieldmapper;

import com.bervan.dtocore.model.CustomMapper;

public class ToUpperCaseMapper implements CustomMapper<String, String> {
    @Override
    public String map(String obj) {
        return obj.toUpperCase();
    }
}
