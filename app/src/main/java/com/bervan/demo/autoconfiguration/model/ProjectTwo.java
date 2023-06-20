package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.HistoryCollection;
import com.bervan.history.model.HistorySupported;
import lombok.*;

import javax.persistence.*;
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
public class ProjectTwo implements AbstractBaseEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    @ManyToOne
    private UserTwo creator;
    @OneToMany
    @HistoryCollection(historyClass = ProjectHistoryTwo.class)
    private Set<ProjectHistoryTwo> history = new HashSet<>();

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
