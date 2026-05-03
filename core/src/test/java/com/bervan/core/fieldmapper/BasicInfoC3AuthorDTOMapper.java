package com.bervan.core.fieldmapper;

import com.bervan.core.model.CustomMapper;

import java.lang.reflect.Field;

public class BasicInfoC3AuthorDTOMapper implements CustomMapper<String, C3Author> {
    @Override
    public C3Author map(String obj, Field fromField, Field toField) {
        C3Author c3Author = new C3Author();
        //parse string or query to db not make sense to do it in the test

        return c3Author;
    }
}
