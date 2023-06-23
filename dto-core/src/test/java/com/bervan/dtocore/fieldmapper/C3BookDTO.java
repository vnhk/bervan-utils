package com.bervan.dtocore.fieldmapper;


import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class C3BookDTO implements BaseDTO<Long> {
    private Long id;
    private String name;
    private String summary;
    private String author;

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
        return C3Book.class;
    }
}
