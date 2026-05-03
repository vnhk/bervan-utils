package com.bervan.core.c3;

import com.bervan.core.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Task implements BaseModel<UUID> {
    private UUID id;
    private String name;
    private List<TaskRelation> parentRelations = new LinkedList<>();
    private List<TaskRelation> childRelations = new LinkedList<>();
}
