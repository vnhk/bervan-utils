package com.bervan.history.diff.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DiffAttribute {
    private final String attribute;
    private final List<DiffWord> diff;
}
