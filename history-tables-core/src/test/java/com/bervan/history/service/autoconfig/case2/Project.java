package com.bervan.history.service.autoconfig.case2;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.HistoryCollection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    @HistoryCollection(historyClass = ProjectHistory.class)
    private Set<ProjectHistory> history;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
