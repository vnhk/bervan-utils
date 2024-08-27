package com.bervan.core.c1;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;

public class Book implements BaseDTOTarget<Long> {

    private Long id;
    private String name;
    private String summary;
    private String secureField; //no field in DTO
    private Author author; //complex object with DTO

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTO<Long>> dto() {
        return BookDTO.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSecureField() {
        return secureField;
    }

    public void setSecureField(String secureField) {
        this.secureField = secureField;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public static final class BookBuilder {
        private Long id;
        private String name;
        private String summary;
        private String secureField;
        private Author author;

        private BookBuilder() {
        }

        public static BookBuilder aBook() {
            return new BookBuilder();
        }

        public BookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BookBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public BookBuilder secureField(String secureField) {
            this.secureField = secureField;
            return this;
        }

        public BookBuilder author(Author author) {
            this.author = author;
            return this;
        }

        public Book build() {
            Book book = new Book();
            book.setId(id);
            book.setName(name);
            book.setSummary(summary);
            book.setSecureField(secureField);
            book.setAuthor(author);
            return book;
        }
    }
}
