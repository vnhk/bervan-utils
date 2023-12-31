package com.bervan.core.fieldmapper;

import com.bervan.core.model.DefaultCustomMapper;

public class DefaultC3StringToAuthorCustomMapper implements DefaultCustomMapper<String, C3Author> {
    @Override
    public C3Author map(String c3Author) {
        return null;
    }

    @Override
    public Class<String> getFrom() {
        return String.class;
    }

    @Override
    public Class<C3Author> getTo() {
        return C3Author.class;
    }
}
