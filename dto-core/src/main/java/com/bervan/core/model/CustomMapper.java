package com.bervan.core.model;

public interface CustomMapper<FROM, TO> {
    TO map(FROM obj);
}
