package com.bervan.history.service.case2;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class Project implements AbstractBaseEntity<UUID> {

    private UUID id;
    private String name;
    private String summary;
    private String description;
    private BigDecimal price;
    private Integer importance;
    private ProjectStatus status;
    private LocalDateTime modificationDate;

    //history
    private Set<ProjectHistory> history;

    public Project() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Set<ProjectHistory> getHistory() {
        return history;
    }

    public void setHistory(Set<ProjectHistory> history) {
        this.history = history;
    }

    public Project(UUID id, String name, String summary, String description, BigDecimal price, Integer importance, ProjectStatus status, LocalDateTime modificationDate, Set<ProjectHistory> history) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.price = price;
        this.importance = importance;
        this.status = status;
        this.modificationDate = modificationDate;
        this.history = history;
    }

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

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}
