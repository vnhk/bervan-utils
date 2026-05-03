package com.bervan.core.c3;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.FieldMapperConfig;
import com.bervan.core.model.PostCustomMappers;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@PostCustomMappers(mappers = {DirectionCustomMapper.class})
public class TaskRelationDto implements BaseDTO<UUID> {
    private UUID id;
    private TaskDto parent;
    private TaskDto child;
    @FieldMapperConfig(targetFieldNames = {"parent.name"})
    private String taskParentName;
    private TaskRelationType type;
    private String direction;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return TaskRelation.class;
    }
}
