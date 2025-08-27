package com.bervan.core.fieldmapper;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.FieldCustomMapper;


public class C3BookDTO implements BaseDTO<Long> {
    private Long id;
    private String name;
    private String summary;
    @FieldCustomMapper(mapper = BasicInfoC3AuthorDTOMapper.class)
    private String author;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseModel<Long>> dtoTarget() {
        return C3Book.class;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static final class C3BookDTOBuilder {
        private Long id;
        private String name;
        private String summary;
        private String author;

        private C3BookDTOBuilder() {
        }

        public static C3BookDTOBuilder aC3BookDTO() {
            return new C3BookDTOBuilder();
        }

        public C3BookDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C3BookDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public C3BookDTOBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public C3BookDTOBuilder author(String author) {
            this.author = author;
            return this;
        }

        public C3BookDTO build() {
            C3BookDTO c3BookDTO = new C3BookDTO();
            c3BookDTO.setId(id);
            c3BookDTO.setName(name);
            c3BookDTO.setSummary(summary);
            c3BookDTO.setAuthor(author);
            return c3BookDTO;
        }
    }
}
