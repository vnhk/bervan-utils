package com.bervan.core.model;

public interface BaseDTO<ID> {
    void setId(ID id);

    ID getId();

    Class<? extends BaseModel<ID>> dtoTarget();
}
