package com.bervan.history.service;

import com.bervan.history.diff.model.DiffAttribute;
import com.bervan.history.diff.service.DiffService;
import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.Persistable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    public List<DiffAttribute> compare(AbstractBaseEntity<ID> entity, AbstractBaseHistoryEntity<ID> history) {
        try {
            List<DiffAttribute> diffAttributes = new ArrayList<>();
            List<Field> historyFields = getHistoryFields(history);
            for (Field historyField : historyFields) {
                Field entityField = entity.getClass().getDeclaredField(historyField.getName());
                HistoryField annotation = historyField.getAnnotation(HistoryField.class);
                diffAttributes.add(new DiffAttribute(historyField.getName(),
                        DiffService.diff(getHistoryFieldValue(historyField, history), getEntityFieldValue(entityField, entity, annotation))));
            }

            return diffAttributes;

        } catch (Exception e) {
            log.error("Diff cannot be performed!", e);
            throw new RuntimeException("Diff cannot be performed!");
        }
    }

    /**
     * @param field      AbstractBaseEntity main field with substitute in AbstractBaseHistoryEntity field that is annotated
     *                   with @HistoryField, for example String name / Status status / Project project
     * @param entity     AbstractBaseEntity object
     * @param annotation HistoryField annotation with path
     * @return field value as String
     * @throws IllegalAccessException when field is not accessible
     * @throws NoSuchFieldException   when path is invalid
     */
    private String getEntityFieldValue(Field field, AbstractBaseEntity<ID> entity, HistoryField annotation) throws IllegalAccessException, NoSuchFieldException {
        String path = annotation.comparePath();
        if (Strings.isNotBlank(path)) {
            return getFieldValueByAnnotationPath(field, entity, path);
        }

        field.setAccessible(true);
        String value = String.valueOf(field.get(entity));
        field.setAccessible(false);

        return value;
    }

    /**
     * History fields in AbstractBaseHistoryEntity classes must be of String type or with toString() method (Enums).
     * Therefore, no additional logic is needed to obtain their value
     *
     * @param field  AbstractBaseHistoryEntity field
     * @param entity AbstractBaseHistoryEntity
     * @return field value as String
     * @throws IllegalAccessException when field is not accessible
     */
    private String getHistoryFieldValue(Field field, AbstractBaseHistoryEntity entity) throws IllegalAccessException {
        field.setAccessible(true);
        Object obj = field.get(entity);
        String value = null;
        if (obj != null) {
            value = String.valueOf(obj);
        }
        field.setAccessible(false);

        return value;
    }

    /**
     * When performing the diff, sometimes the algorithm encounters a BaseEntity complex field (eq. Status / Project / User).
     * Diff service requires all comparable data to be text. @HistoryField annotation has a path attribute that can be used to define field.
     *
     * @param field  main field annotated with @HistoryField, for example String name / Status status / Project project
     * @param object AbstractBaseHistory or AbstractBaseEntity object
     * @return field value based on the @HistoryField path, if path is blank then main @param field is returned
     * @throws IllegalAccessException when field is not accessible
     * @throws NoSuchFieldException   when path is invalid
     */
    private String getFieldValueByAnnotationPath(Field field, Object object, String path) throws IllegalAccessException, NoSuchFieldException {
        if (Strings.isNotBlank(path)) {
            for (String p : path.split("\\.")) {
                field.setAccessible(true);
                object = field.get(object);
                if (object == null) {
                    return null;
                }
                field.setAccessible(false);
                field = object.getClass().getDeclaredField(p);
            }
        }

        field.setAccessible(true);
        String res = String.valueOf(field.get(object));
        field.setAccessible(false);

        return res;
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
            Object val = getVal(baseEntity, historyField);
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

    private Object getVal(Object obj, Field historyField) throws Exception {
        String path = historyField.getAnnotation(HistoryField.class).savePath();
        Field entityField;
        if (Strings.isNotBlank(path)) {
            String[] values = path.split("\\.");
            //example in Project: User owner->owner.nick
            entityField = getField(obj, values[0]);
            obj = getObjFromPathPart(obj, entityField);
            for (int i = 1; i < values.length; i++) {
                String fieldName = values[i];
                entityField = getField(obj, fieldName);
                obj = getObjFromPathPart(obj, entityField);
            }

            return obj;
        } else {
            String name = historyField.getName();
            entityField = obj.getClass().getDeclaredField(name);
            return getObjFromPathPart(obj, entityField);
        }
    }

    private Field getField(Object entity, String fieldName) throws NoSuchFieldException {
        return entity.getClass().getDeclaredField(fieldName);
    }

    private Object getObjFromPathPart(Object entity, Field entityField) throws IllegalAccessException {
        entityField.setAccessible(true);
        entity = entityField.get(entity);
        entityField.setAccessible(false);
        return entity;
    }
}
