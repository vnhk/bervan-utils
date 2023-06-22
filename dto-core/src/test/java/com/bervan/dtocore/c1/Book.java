package com.bervan.dtocore.c1;

import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.BaseDTOTarget;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book implements BaseDTOTarget<Long> {

    private Long id;
    private String name;
    private String summary;
    private String secureField; //no field in DTO
    private Author author; //complex object with DTO

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<? extends BaseDTO<Long>> dto() {
        return BookDTO.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
