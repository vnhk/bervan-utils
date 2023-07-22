package com.bervan.core.c2;

import com.bervan.core.model.DefaultCustomMapper;

public class C2AuthorMapper implements DefaultCustomMapper<C2Author, Long> {
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
