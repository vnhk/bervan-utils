package com.bervan.core.c3;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.FieldMapperConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TaskDto implements BaseDTO<UUID> {
    @FieldMapperConfig(targetFieldNames = {"parentRelations", "childRelations"})
    private List<TaskRelationDto> relations = new LinkedList<>();
    private UUID id;
    private String name;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return Task.class;
    }
}
