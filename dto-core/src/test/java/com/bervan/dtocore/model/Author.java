package com.bervan.dtocore.model;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Author implements BaseDTOTarget<Long> {
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
        return AuthorDTO.class;
    }

}
