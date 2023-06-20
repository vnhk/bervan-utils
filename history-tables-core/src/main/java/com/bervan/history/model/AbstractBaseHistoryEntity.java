package com.bervan.history.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public interface AbstractBaseHistoryEntity<ID> extends Serializable, Persistable<ID> {
    default void buildTargetEntityConnection(AbstractBaseEntity<ID> entity) {
        Collection historyEntities = entity.getHistoryEntities();
        if (historyEntities == null) {
            historyEntities = new HashSet<>();
            entity.setHistoryEntities(historyEntities);
        }
        historyEntities.add(this);
        this.setEntity(entity);
    }

    default void setEntity(AbstractBaseEntity<ID> entity) {
        Field field = getHistoryOwnerEntityField(this);
        try {
            field.setAccessible(true);
            field.set(this, entity);
        } catch (Exception e) {
            throw new RuntimeException("Automatic setup failed! Override setEntity method for custom configuration!");
        } finally {
            field.setAccessible(false);
        }
    }

    default Field getHistoryOwnerEntityField(AbstractBaseHistoryEntity<ID> historyEntity) {
        List<Field> fields = Arrays.stream(historyEntity.getClass().getDeclaredFields())
                .filter(e -> e.isAnnotationPresent(HistoryOwnerEntity.class))
                .collect(Collectors.toList());

        if (fields.size() != 1) {
            throw new RuntimeException("Could not find field for entity in history class! " +
                    "Automatic setup failed! Make sure history target class field is annotated with HistoryOwnerEntity " +
                    "or override methods for custom configuration!");
        }

        return fields.get(0);
    }

    default AbstractBaseEntity<ID> getEntity() {
        Field field = getHistoryOwnerEntityField(this);

        try {
            field.setAccessible(true);
            return (AbstractBaseEntity<ID>) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Automatic setup failed! Override getEntity method for custom configuration!");
        } finally {
            field.setAccessible(false);
        }
    }
}
