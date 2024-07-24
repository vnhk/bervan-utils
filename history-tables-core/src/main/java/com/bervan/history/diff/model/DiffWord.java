package com.bervan.history.diff.model;

public class DiffWord {
    private final String value;
    private final DiffType type;

    public DiffWord(String value, DiffType type) {
        this.value = value;
        this.type = type;
    }

    public DiffType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type.getDisplayName() + value;
    }
}
