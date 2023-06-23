package com.bervan.dtocore.fieldmapper;

import com.bervan.dtocore.c1.Author;
import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class C3AuthorDTO implements BaseDTO<Long> {
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
}
