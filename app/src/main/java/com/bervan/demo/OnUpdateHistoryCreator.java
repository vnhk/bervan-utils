package com.bervan.demo;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PreUpdate;

@Service
public class OnUpdateHistoryCreator<ID> {

    @PreUpdate
    //NOT WORKING, IT IS EXECUTED AFTER UPDATE!!!
    public void beforeAnyUpdate(Object baseEntity) {
        if (!(baseEntity instanceof AbstractBaseEntity)) {
            throw new RuntimeException("Automatically creation of history is only supported for " + AbstractBaseEntity.class.getName());
        }

        if (((AbstractBaseEntity<?>) baseEntity).getId() == null) {
            return;
        }

        EntityManager entityManager = BeanUtils.getBean(EntityManager.class);

        AbstractBaseEntity<ID> savedObj = (AbstractBaseEntity<ID>)
                entityManager.find(baseEntity.getClass(), ((AbstractBaseEntity<?>) baseEntity).getId());

        HistoryService<ID> historyService = new HistoryService<>();
        AbstractBaseHistoryEntity<ID> history = historyService.buildHistory(savedObj);

        entityManager.persist(history);
    }
}
