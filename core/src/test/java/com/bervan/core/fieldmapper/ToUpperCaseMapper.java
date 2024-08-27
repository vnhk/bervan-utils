package com.bervan.core.fieldmapper;

import com.bervan.core.model.CustomMapper;

public class ToUpperCaseMapper implements CustomMapper<String, String> {
    @Override
    public String map(String obj) {
        return obj.toUpperCase();
    }
}
