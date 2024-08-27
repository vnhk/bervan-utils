package com.bervan.core.fieldmapper;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;
import com.bervan.core.model.FieldCustomMapper;

public class C3Book implements BaseDTOTarget<Long> {

    private Long id;
    @FieldCustomMapper(mapper = ToUpperCaseMapper.class)
    private String name;
    private String summary;
    private String secureField; //no field in DTO
    @FieldCustomMapper(mapper = BasicInfoC3AuthorMapper.class)
    private C3Author author; //complex object that we want to change using fieldmapper

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTO<Long>> dto() {
        return C3BookDTO.class;
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

    public C3Author getAuthor() {
        return author;
    }

    public void setAuthor(C3Author author) {
        this.author = author;
    }

    public static final class C3BookBuilder {
        private Long id;
        private String name;
        private String summary;
        private String secureField;
        private C3Author author;

        private C3BookBuilder() {
        }

        public static C3BookBuilder aC3Book() {
            return new C3BookBuilder();
        }

        public C3BookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C3BookBuilder name(String name) {
            this.name = name;
            return this;
        }

        public C3BookBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public C3BookBuilder secureField(String secureField) {
            this.secureField = secureField;
            return this;
        }

        public C3BookBuilder author(C3Author author) {
            this.author = author;
            return this;
        }

        public C3Book build() {
            C3Book c3Book = new C3Book();
            c3Book.setId(id);
            c3Book.setName(name);
            c3Book.setSummary(summary);
            c3Book.setSecureField(secureField);
            c3Book.setAuthor(author);
            return c3Book;
        }
    }
}
