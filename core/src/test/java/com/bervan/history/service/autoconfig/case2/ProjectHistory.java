package com.bervan.history.service.autoconfig.case2;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistoryOwnerEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectHistory implements AbstractBaseHistoryEntity<UUID> {
    //project history id if want to save it to the database
    private UUID id;
    @HistoryField
    private String name;
    @HistoryField
    private String summary;

    //decided to not have history for description, can be removed from ProjectHistory
    private String description;

    @HistoryField
    private BigDecimal price;
    @HistoryField
    private Integer importance;
    @HistoryField
    private ProjectStatus status;

    @HistoryOwnerEntity
    private Project project;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
