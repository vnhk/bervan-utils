package com.bervan.core.c1;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;

public class AuthorDTO implements BaseDTO<Long> {
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
    public Class<? extends BaseDTOTarget<Long>> dtoTarget() {
        return Author.class;
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

    public static final class AuthorDTOBuilder {
        private Long id;
        private String firstName;
        private String lastName;

        private AuthorDTOBuilder() {
        }

        public static AuthorDTOBuilder anAuthorDTO() {
            return new AuthorDTOBuilder();
        }

        public AuthorDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthorDTOBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AuthorDTOBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AuthorDTO build() {
            AuthorDTO authorDTO = new AuthorDTO();
            authorDTO.setId(id);
            authorDTO.setFirstName(firstName);
            authorDTO.setLastName(lastName);
            return authorDTO;
        }
    }
}
