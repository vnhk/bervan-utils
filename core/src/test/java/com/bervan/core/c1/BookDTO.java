package com.bervan.core.c1;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;

public class BookDTO implements BaseDTO<Long> {
    private Long id;
    private String name;
    private String summary;
    private AuthorDTO author;
    private String anotherSecuredField; //no field in DTO Target


    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTOTarget<Long>> dtoTarget() {
        return Book.class;
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

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    public String getAnotherSecuredField() {
        return anotherSecuredField;
    }

    public void setAnotherSecuredField(String anotherSecuredField) {
        this.anotherSecuredField = anotherSecuredField;
    }

    public static final class BookDTOBuilder {
        private Long id;
        private String name;
        private String summary;
        private AuthorDTO author;
        private String anotherSecuredField;

        private BookDTOBuilder() {
        }

        public static BookDTOBuilder aBookDTO() {
            return new BookDTOBuilder();
        }

        public BookDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BookDTOBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public BookDTOBuilder author(AuthorDTO author) {
            this.author = author;
            return this;
        }

        public BookDTOBuilder anotherSecuredField(String anotherSecuredField) {
            this.anotherSecuredField = anotherSecuredField;
            return this;
        }

        public BookDTO build() {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(id);
            bookDTO.setName(name);
            bookDTO.setSummary(summary);
            bookDTO.setAuthor(author);
            bookDTO.setAnotherSecuredField(anotherSecuredField);
            return bookDTO;
        }
    }
}
