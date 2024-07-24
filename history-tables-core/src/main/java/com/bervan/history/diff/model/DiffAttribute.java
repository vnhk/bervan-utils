package com.bervan.history.diff.model;


import java.util.List;

public class DiffAttribute {
    private final String attribute;
    private final List<DiffWord> diff;

    public DiffAttribute(String attribute, List<DiffWord> diff) {
        this.attribute = attribute;
        this.diff = diff;
    }

    public String getAttribute() {
        return attribute;
    }

    public List<DiffWord> getDiff() {
        return diff;
    }
}
