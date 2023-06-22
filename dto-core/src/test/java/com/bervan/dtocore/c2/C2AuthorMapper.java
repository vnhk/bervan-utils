package com.bervan.dtocore.c2;

import com.bervan.dtocore.service.CustomMapper;

public class C2AuthorMapper implements CustomMapper<C2Author, Long> {
    @Override
    public Long map(C2Author c2Author) {
        return c2Author.getId();
    }

    @Override
    public Class<C2Author> getFrom() {
        return C2Author.class;
    }

    @Override
    public Class<Long> getTo() {
        return Long.class;
    }
}
