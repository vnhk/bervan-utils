package com.bervan.dtocore.fieldmapper;

import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
import com.bervan.dtocore.model.FieldCustomMapper;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class C3Book implements BaseDTOTarget<Long> {

    private Long id;
    @FieldCustomMapper(mapper = ToUpperCaseMapper.class)
    private String name;
    private String summary;
    private String secureField; //no field in DTO
    @FieldCustomMapper(mapper = BasicInfoC3AuthorMapper.class)
    private C3Author author; //complex object that we want to change using fieldmapper

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTO<Long>> dto() {
        return C3BookDTO.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
