package com.bervan.demo.manualconfiguration.model;

import com.bervan.demo.OnUpdateHistoryCreator;
import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistorySupported;
import javax.persistence.*;

import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
//@EntityListeners(OnUpdateHistoryCreator.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectOne implements AbstractBaseEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    @ManyToOne
    private UserOne creator;
    @OneToMany
    private Set<ProjectHistoryOne> history = new HashSet<>();

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
        this.history = (Set<ProjectHistoryOne>) abstractBaseHistoryEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<UUID>> getTargetHistoryEntityClass() {
        return ProjectHistoryOne.class;
    }
}
