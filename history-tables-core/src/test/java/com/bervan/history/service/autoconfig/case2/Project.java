package com.bervan.history.service.autoconfig.case2;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.HistoryCollection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @HistoryCollection(historyClass = ProjectHistory.class)
    private Set<ProjectHistory> history;
    private LocalDateTime modificationDate;

    public Project(UUID id, String name, String summary, String description, BigDecimal price, Integer importance, ProjectStatus status, Set<ProjectHistory> history, LocalDateTime modificationDate) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.price = price;
        this.importance = importance;
        this.status = status;
        this.history = history;
        this.modificationDate = modificationDate;
    }

    public Project(UUID id, String name, String summary, String description, BigDecimal price, int importance, ProjectStatus status) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.price = price;
        this.importance = importance;
    }

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
