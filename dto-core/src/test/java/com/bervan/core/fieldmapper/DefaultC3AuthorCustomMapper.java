package com.bervan.core.fieldmapper;

import com.bervan.core.model.DefaultCustomMapper;

public class DefaultC3AuthorCustomMapper implements DefaultCustomMapper<C3Author, String> {
    @Override
    public String map(C3Author c3Author) {
        return c3Author.getId().toString();
    }

    @Override
    public Class<C3Author> getFrom() {
        return C3Author.class;
    }

    @Override
    public Class<String> getTo() {
        return String.class;
    }
}
