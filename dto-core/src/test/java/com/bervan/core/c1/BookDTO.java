package com.bervan.core.c1;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
}
