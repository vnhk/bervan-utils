package com.bervan.dtocore.fieldmapper;

import com.bervan.dtocore.model.CustomMapper;

public class BasicInfoC3AuthorDTOMapper implements CustomMapper<String, C3Author> {
    @Override
    public C3Author map(String obj) {
        C3Author c3Author = new C3Author();
        //parse string or query to db not make sense to do it in the test

        return c3Author;
    }
}
