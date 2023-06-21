package com.bervan.dtocore.model;


import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDTO implements BaseDTO<Long>{
    private Long id;
    private String name;
    private String summary;

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
}
