package com.bervan.entities;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HistoryService<ID> {
    public AbstractBaseHistoryEntity<ID> buildHistory(AbstractBaseEntity<ID> baseEntity) {
        AbstractBaseHistoryEntity<ID> historyEntity = initHistoryEntity(baseEntity);
        buildHistory(baseEntity, historyEntity);
        return historyEntity;
    }

    private void buildHistory(AbstractBaseEntity<ID> baseEntity, AbstractBaseHistoryEntity<ID> historyEntity) {
        List<Field> historyFields = getHistoryFields(historyEntity);

        for (Field historyField : historyFields) {
            copyFieldToHistory(baseEntity, historyEntity, historyField);
        }

        setTargetEntity(historyEntity, baseEntity);
    }

    private void copyFieldToHistory(AbstractBaseEntity<ID> baseEntity, AbstractBaseHistoryEntity<ID> historyEntity, Field historyField) {
        try {
            String name = historyField.getName();
            Field entityField = baseEntity.getClass().getDeclaredField(name);
            Object val = getVal(baseEntity, entityField, historyField);
            setFieldVal(historyEntity, historyField, val);
        } catch (Exception e) {
            log.error("Could not copy " + historyField.getName() + "!", e);
            throw new RuntimeException(e);
        }
    }

    private List<Field> getHistoryFields(AbstractBaseHistoryEntity<ID> historyEntity) {
        return Arrays.stream(historyEntity.getClass().getDeclaredFields())
                .filter(e -> e.isAnnotationPresent(HistoryField.class))
                .filter(e -> !e.getAnnotation(HistoryField.class).isTargetEntity())
                .collect(Collectors.toList());
    }

    private AbstractBaseHistoryEntity<ID> initHistoryEntity(AbstractBaseEntity<ID> baseEntity) {
        Class<? extends AbstractBaseHistoryEntity<ID>> targetHistoryEntityClass = baseEntity.getTargetHistoryEntityClass();
        try {
            return targetHistoryEntityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("History entity must have no args public constructor!", e);
            throw new RuntimeException(e);
        }
    }

    private void setFieldVal(Persistable<ID> entity, Field entityField, Object val) throws IllegalAccessException {
//        if (val instanceof Long && entityField.getType().getSuperclass().equals(BaseEntity.class)) {
//            //it means that we try to set ID (Long) as a BaseEntityClass ex. UserDetails owner in Project = 1L;
//            val = entityManager.find(entityField.getType(), val);
        //
//        }
        entityField.setAccessible(true);
        entityField.set(entity, val);
        entityField.setAccessible(false);
    }

    private void setTargetEntity(AbstractBaseHistoryEntity<ID> history, AbstractBaseEntity<ID> entity) {
        //required if want to have automatically connected history and entity by database relation!
        history.buildTargetEntityConnection(entity);
    }

    private Object getVal(Object entity, Field entityField, Field historyField) throws Exception {
        String path = historyField.getAnnotation(HistoryField.class).savePath();
        if (Strings.isNotBlank(path)) {
            String[] values = path.split("\\.");
            //example in Project: User owner->owner.nick
            for (String value : values) {
                entityField.setAccessible(true);
                entity = entityField.get(entity);
                entityField.setAccessible(false);
                entityField = entityField.getType().getDeclaredField(value);
            }
        }
        Object res = null;
        if (entity != null) {
            entityField.setAccessible(true);
            res = entityField.get(entity);
            entityField.setAccessible(false);
        }

        return res;
    }
}
