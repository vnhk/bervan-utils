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
public class User implements AbstractBaseEntity<Long> {
    private Long id;
    private String nick;
    private String name;
    private String surname;
    private LocalDateTime modificationDate;

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public Set<? extends AbstractBaseHistoryEntity<Long>> getHistoryEntities() {
        return null;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<Long>> abstractBaseHistoryEntities) {

    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<Long>> getTargetHistoryEntityClass() {
        return null;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
