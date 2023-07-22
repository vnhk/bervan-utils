package com.bervan.core.fieldmapper;

import com.bervan.core.c1.Author;
import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;
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
