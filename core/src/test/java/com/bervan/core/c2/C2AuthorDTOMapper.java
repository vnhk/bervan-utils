package com.bervan.core.c2;

import com.bervan.core.model.DefaultCustomMapper;

public class C2AuthorDTOMapper implements DefaultCustomMapper<Long, C2Author> {
    @Override
    public C2Author map(Long id) {
        C2Author author = new C2Author();
        author.setId(id);
        return author;
    }

    @Override
    public Class<Long> getFrom() {
        return Long.class;
    }

    @Override
    public Class<C2Author> getTo() {
        return C2Author.class;
    }
}
