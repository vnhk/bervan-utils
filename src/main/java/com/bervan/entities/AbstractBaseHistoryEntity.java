package com.bervan.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public interface AbstractBaseHistoryEntity<ID> extends Serializable, Persistable<ID> {
    default void buildTargetEntityConnection(AbstractBaseEntity<ID> entity) {
        Set historyEntities = entity.getHistoryEntities();
        if (historyEntities == null) {
            historyEntities = new HashSet();
            entity.setHistoryEntities(historyEntities);
        }
        historyEntities.add(this);
        this.setEntity(entity);
    }

    void setEntity(AbstractBaseEntity<ID> entity);

    AbstractBaseEntity<ID> getEntity();
}
