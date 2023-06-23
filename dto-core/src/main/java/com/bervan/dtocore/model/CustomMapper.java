package com.bervan.dtocore.model;

public interface CustomMapper<FROM, TO> {
    TO map(FROM obj);
}
