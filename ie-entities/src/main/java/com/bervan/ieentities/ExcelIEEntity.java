package com.bervan.ieentities;

public interface ExcelIEEntity<ID> {
    @ExcelIgnore
    ID getId();

    @ExcelIgnore
    void setId(ID id);
}
