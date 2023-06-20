package com.bervan.dtocore.model;

public interface BaseDTOTarget<ID> {
    void setId(ID id);

    ID getId();

    Class<? extends BaseDTO<ID>> dto();
}
