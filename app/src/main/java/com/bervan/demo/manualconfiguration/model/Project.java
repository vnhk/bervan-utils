package com.bervan.demo.manualconfiguration.model;

import com.bervan.demo.OnUpdateHistoryCreator;
import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(OnUpdateHistoryCreator.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project implements AbstractBaseEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    @ManyToOne
    private User creator;
    @OneToMany
    private Set<ProjectHistory> history = new HashSet<>();

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Set<? extends AbstractBaseHistoryEntity<UUID>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<UUID>> abstractBaseHistoryEntities) {
        this.history = (Set<ProjectHistory>) abstractBaseHistoryEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<UUID>> getTargetHistoryEntityClass() {
        return ProjectHistory.class;
    }
}
