package com.bervan.demo.manualconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistorySupported;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Entity
//@EntityListeners(OnUpdateHistoryCreator.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectOne implements AbstractBaseEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    private UserOne creator;
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    private Set<ProjectHistoryOne> history = new HashSet<>();

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Set<? extends AbstractBaseHistoryEntity<Long>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<Long>> abstractBaseHistoryEntities) {
        this.history = (Set<ProjectHistoryOne>) abstractBaseHistoryEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<Long>> getTargetHistoryEntityClass() {
        return ProjectHistoryOne.class;
    }
}
