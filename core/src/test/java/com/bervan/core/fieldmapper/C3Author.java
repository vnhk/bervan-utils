package com.bervan.core.fieldmapper;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;

public class C3Author implements BaseDTOTarget<Long> {
    private Long id;
    private String firstName;
    private String lastName;

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTO<Long>> dto() {
        return C3AuthorDTO.class;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static final class C3AuthorBuilder {
        private Long id;
        private String firstName;
        private String lastName;

        private C3AuthorBuilder() {
        }

        public static C3AuthorBuilder aC3Author() {
            return new C3AuthorBuilder();
        }

        public C3AuthorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C3AuthorBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public C3AuthorBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public C3Author build() {
            C3Author c3Author = new C3Author();
            c3Author.setId(id);
            c3Author.setFirstName(firstName);
            c3Author.setLastName(lastName);
            return c3Author;
        }
    }
}
