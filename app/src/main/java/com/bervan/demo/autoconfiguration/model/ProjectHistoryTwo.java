package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistoryOwnerEntity;
import com.bervan.history.model.HistorySupported;
import javax.persistence.*;

import com.bervan.ieentities.ExcelIEEntity;
import lombok.*;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectHistoryTwo implements AbstractBaseHistoryEntity<Long>, ExcelIEEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;

    @HistoryField
    private String name;

    //ignored in history
    private String description;

    @HistoryField(savePath = "creator.id")
    private Long creator;
    @ManyToOne
    @HistoryOwnerEntity
    private ProjectTwo project;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
