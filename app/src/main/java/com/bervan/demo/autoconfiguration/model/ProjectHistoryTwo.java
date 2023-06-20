package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistoryOwnerEntity;
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
public class ProjectHistoryTwo implements AbstractBaseHistoryEntity<UUID> {
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
    @HistoryOwnerEntity
    private ProjectTwo project;

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
