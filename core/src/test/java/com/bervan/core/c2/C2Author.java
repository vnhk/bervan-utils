package com.bervan.core.c2;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;

public class C2Author implements BaseDTOTarget<Long> {
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
        return C2AuthorDTO.class;
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

    public static final class C2AuthorBuilder {
        private Long id;
        private String firstName;
        private String lastName;

        private C2AuthorBuilder() {
        }

        public static C2AuthorBuilder aC2Author() {
            return new C2AuthorBuilder();
        }

        public C2AuthorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public C2AuthorBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public C2AuthorBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public C2Author build() {
            C2Author c2Author = new C2Author();
            c2Author.setId(id);
            c2Author.setFirstName(firstName);
            c2Author.setLastName(lastName);
            return c2Author;
        }
    }
}
