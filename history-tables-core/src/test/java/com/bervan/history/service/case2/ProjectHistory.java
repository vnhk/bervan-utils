package com.bervan.history.service.case2;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectHistory implements AbstractBaseHistoryEntity<UUID> {
    //project history id if want to save it to the database
    private UUID id;
    @HistoryField
    private String name;
    @HistoryField
    private String summary;

    //decided to not have history for description, can be removed from ProjectHistory
    private String description;

    @HistoryField
    private BigDecimal price;
    @HistoryField
    private Integer importance;
    @HistoryField
    private ProjectStatus status;

    private LocalDateTime updateDate;

    public ProjectHistory() {

    }

    public ProjectHistory(UUID id, String name, String summary, String description, BigDecimal price, Integer importance, ProjectStatus status, LocalDateTime updateDate, Project project) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.price = price;
        this.importance = importance;
        this.status = status;
        this.updateDate = updateDate;
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    //relation
    private Project project;

    @Override
    public void setEntity(AbstractBaseEntity<UUID> entity) {
        this.project = (Project) entity;
    }

    @Override
    public AbstractBaseEntity<UUID> getEntity() {
        return project;
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
