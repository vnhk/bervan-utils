package com.bervan.history.service.case2;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Project implements AbstractBaseEntity<UUID> {

    private UUID id;
    private String name;
    private String summary;
    private String description;
    private BigDecimal price;
    private Integer importance;
    private ProjectStatus status;

    //history
    private Set<ProjectHistory> history;

    @Override
    public Set<? extends AbstractBaseHistoryEntity<UUID>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<UUID>> historyEntities) {
        this.history = (Set<ProjectHistory>) historyEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<UUID>> getTargetHistoryEntityClass() {
        return ProjectHistory.class;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
