package com.bervan.core.model;

public interface BaseDTOTarget<ID> {
    void setId(ID id);

    ID getId();

    Class<? extends BaseDTO<ID>> dto();
}
