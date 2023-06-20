package com.bervan.history.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface AbstractBaseEntity<ID> extends Serializable, Persistable<ID> {
    default Collection<? extends AbstractBaseHistoryEntity<ID>> getHistoryEntities() {
        Field field = getHistoryCollectionField();

        try {
            field.setAccessible(true);
            return (Collection<? extends AbstractBaseHistoryEntity<ID>>) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Automatic setup failed! Override getHistoryEntities method for custom configuration!");
        } finally {
            field.setAccessible(false);
        }
    }

    default void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<ID>> historyEntities) {
        Field field = getHistoryCollectionField();

        try {
            field.setAccessible(true);
            field.set(this, historyEntities);
        } catch (Exception e) {
            throw new RuntimeException("Automatic setup failed! Override setHistoryEntities method for custom configuration!");
        } finally {
            field.setAccessible(false);
        }
    }

    default Class<? extends AbstractBaseHistoryEntity<ID>> getTargetHistoryEntityClass() {
        Field field = getHistoryCollectionField();
        return (Class<? extends AbstractBaseHistoryEntity<ID>>) field.getAnnotation(HistoryCollection.class).historyClass();
    }

    default Field getHistoryCollectionField() {
        List<Field> fields = getFieldsAnnotatedWithHistoryCollection();

        if (fields.size() != 1) {
            throw new RuntimeException("Could not find field for history! " +
                    "Automatic setup failed! Make sure history field is annotated with HistoryCollection " +
                    "or override methods for custom configuration!");
        }

        return fields.get(0);
    }

    default List<Field> getFieldsAnnotatedWithHistoryCollection() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(e -> e.isAnnotationPresent(HistoryCollection.class))
                .collect(Collectors.toList());
    }
}
