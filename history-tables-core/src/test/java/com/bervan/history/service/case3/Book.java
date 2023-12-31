package com.bervan.history.service.case3;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Builder
public class Book implements AbstractBaseEntity<Long> {

    private Long id;
    private String name;
    private String summary;
    private User user;
    private LocalDateTime modificationDate;

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    //history
    private Set<BookHistory> history;

    @Override
    public Set<? extends AbstractBaseHistoryEntity<Long>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<Long>> historyEntities) {
        this.history = (Set<BookHistory>) historyEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<Long>> getTargetHistoryEntityClass() {
        return BookHistory.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
