package com.bervan.entities;

public interface Persistable<ID> {
    ID getId();

    void setId(ID id);
}
