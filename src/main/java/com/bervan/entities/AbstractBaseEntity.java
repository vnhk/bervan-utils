package com.bervan.entities;

import java.io.Serializable;
import java.util.Set;

public interface AbstractBaseEntity<ID> extends Serializable, Persistable<ID> {
    Set<? extends AbstractBaseHistoryEntity<ID>> getHistoryEntities();

    void setHistoryEntities(Set<? extends AbstractBaseHistoryEntity<ID>> historyEntities);

    Class<? extends AbstractBaseHistoryEntity<ID>> getTargetHistoryEntityClass();
}
