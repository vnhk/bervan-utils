package com.bervan.core.c3;

import com.bervan.core.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TaskRelation implements BaseModel<UUID> {
    private UUID id;
    private Task parent;
    private Task child;
    private TaskRelationType type;
}
