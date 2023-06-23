package com.bervan.dtocore.fieldmapper;

import com.bervan.dtocore.model.CustomMapper;

public class BasicInfoC3AuthorMapper implements CustomMapper<C3Author, String> {
    @Override
    public String map(C3Author obj) {
        Long id = obj.getId();
        String firstName = obj.getFirstName();
        String lastName = obj.getLastName();

        return "Author with id = " + id + ": " + firstName + " " + lastName;
    }
}
