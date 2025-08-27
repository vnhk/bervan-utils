package com.bervan.core.c1;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
public class Author implements BaseModel<Long> {
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

    public static final class AuthorBuilder {
        private Long id;
        private String firstName;
        private String lastName;

        private AuthorBuilder() {
        }

        public static AuthorBuilder anAuthor() {
            return new AuthorBuilder();
        }

        public AuthorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthorBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AuthorBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Author build() {
            Author author = new Author();
            author.setId(id);
            author.setFirstName(firstName);
            author.setLastName(lastName);
            return author;
        }
    }
}
