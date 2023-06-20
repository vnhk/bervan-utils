package com.bervan.history.diff.model;

public enum DiffType {
    ADDED("+"), REMOVED("-"), EQUAL("=");

    private final String displayName;

    DiffType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }
}
