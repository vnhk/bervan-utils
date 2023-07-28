package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistoryOwnerEntity;
import com.bervan.history.model.HistorySupported;
import com.bervan.ieentities.ExcelIEEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
@EqualsAndHashCode
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

    private LocalDateTime updateDate;

    @Override
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
