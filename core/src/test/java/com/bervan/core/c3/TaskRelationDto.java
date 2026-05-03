package com.bervan.core.c3;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskRelationDto implements BaseDTO<UUID> {
    private UUID id;
    private Task parent;
    private Task child;
    private TaskRelationType type;
    private String direction;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return TaskRelation.class;
    }
}
