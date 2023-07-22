package com.bervan.core.fieldmapper;


import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseDTOTarget;
import com.bervan.core.model.FieldCustomMapper;
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
    @FieldCustomMapper(mapper = BasicInfoC3AuthorDTOMapper.class)
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
