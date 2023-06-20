package com.bervan.demo.manualconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistorySupported;
import javax.persistence.*;

import lombok.*;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectHistoryOne implements AbstractBaseHistoryEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;

    @HistoryField
    private String name;

    //ignored
    private String description;

    @HistoryField(savePath = "creator.id")
    private Long creator;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectOne project;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setEntity(AbstractBaseEntity<Long> entity) {
        this.project = (ProjectOne) entity;
    }

    @Override
    public AbstractBaseEntity<Long> getEntity() {
        return project;
    }
}
