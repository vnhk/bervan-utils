package com.bervan.dtocore.model;

public interface BaseDTO<ID> {
    void setId(ID id);

    ID getId();

    Class<? extends BaseDTOTarget<ID>> dtoTarget();
}
