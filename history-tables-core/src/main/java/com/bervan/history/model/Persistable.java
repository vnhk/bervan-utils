package com.bervan.history.model;

public interface Persistable<ID> {
    ID getId();

    void setId(ID id);
}
