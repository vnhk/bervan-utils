package com.bervan.demo.manualconfiguration.model;

import com.bervan.demo.OnUpdateHistoryCreator;
import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@EntityListeners(OnUpdateHistoryCreator.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectHistory implements AbstractBaseHistoryEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;

    @ManyToOne
    private User creator;
    @ManyToOne
    private Project project;

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setEntity(AbstractBaseEntity<UUID> entity) {
        this.project = (Project) entity;
    }

    @Override
    public AbstractBaseEntity<UUID> getEntity() {
        return project;
    }
}
