package com.bervan.core.c2;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;

public class C2Book implements BaseModel<Long> {

    private Long id;
    private String name;
    private String summary;
    private String secureField; //no field in DTO
    private C2Author author; //complex object with DTO

    @Override
    public Long getId() {
        return id;
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

    public C2Author getAuthor() {
        return author;
    }

    public void setAuthor(C2Author author) {
        this.author = author;
    }

    public static final class C2BookBuilder {
        private Long id;
        private String name;
        private String summary;
        private String secureField;
        private C2Author author;

        private C2BookBuilder() {
        }

        public static C2BookBuilder aC2Book() {
            return new C2BookBuilder();
        }

        public C2BookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C2BookBuilder name(String name) {
            this.name = name;
            return this;
        }

        public C2BookBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public C2BookBuilder secureField(String secureField) {
            this.secureField = secureField;
            return this;
        }

        public C2BookBuilder author(C2Author author) {
            this.author = author;
            return this;
        }

        public C2Book build() {
            C2Book c2Book = new C2Book();
            c2Book.setId(id);
            c2Book.setName(name);
            c2Book.setSummary(summary);
            c2Book.setSecureField(secureField);
            c2Book.setAuthor(author);
            return c2Book;
        }
    }
}
