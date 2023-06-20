package com.bervan.demo.manualconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistorySupported;
import javax.persistence.*;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectHistoryOne implements AbstractBaseHistoryEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @HistoryField
    private String name;

    //ignored
    private String description;

    @HistoryField(savePath = "creator.id")
    private UUID creator;
    @ManyToOne
    private ProjectOne project;

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
        this.project = (ProjectOne) entity;
    }

    @Override
    public AbstractBaseEntity<UUID> getEntity() {
        return project;
    }
}
