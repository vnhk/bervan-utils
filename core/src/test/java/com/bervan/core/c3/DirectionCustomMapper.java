package com.bervan.core.c3;

import com.bervan.core.model.PostMapper;
import org.springframework.stereotype.Service;

@Service
public class DirectionCustomMapper implements PostMapper<TaskRelation, TaskRelationDto> {

    @Override
    public void map(TaskRelation from, TaskRelationDto to) {
        TaskRelationType type = from.getType();

        to.setDirection(type.getDisplayName());
    }

    @Override
    public Class<TaskRelation> getFromType() {
        return TaskRelation.class;
    }

    @Override
    public Class<TaskRelationDto> getToType() {
        return TaskRelationDto.class;
    }
}
