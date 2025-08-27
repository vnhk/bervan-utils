package com.bervan.core.c2;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;

public class C2BookDTO implements BaseDTO<Long> {
    private Long id;
    private String name;
    private String summary;
    private Long author;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
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

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    @Override
    public Class<? extends BaseModel<Long>> dtoTarget() {
        return C2Book.class;
    }

    public static final class C2BookDTOBuilder {
        private Long id;
        private String name;
        private String summary;
        private Long author;

        private C2BookDTOBuilder() {
        }

        public static C2BookDTOBuilder aC2BookDTO() {
            return new C2BookDTOBuilder();
        }

        public C2BookDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C2BookDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public C2BookDTOBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public C2BookDTOBuilder author(Long author) {
            this.author = author;
            return this;
        }

        public C2BookDTO build() {
            C2BookDTO c2BookDTO = new C2BookDTO();
            c2BookDTO.setId(id);
            c2BookDTO.setName(name);
            c2BookDTO.setSummary(summary);
            c2BookDTO.setAuthor(author);
            return c2BookDTO;
        }
    }
}
