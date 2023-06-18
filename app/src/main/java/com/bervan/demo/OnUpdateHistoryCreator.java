package com.bervan.demo;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PreUpdate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OnUpdateHistoryCreator<ID> {
    @Autowired
    private EntityManager entityManager;

    @PreUpdate
    @Transactional
    public void beforeAnyUpdate(Object baseEntity) {
        if (!(baseEntity instanceof AbstractBaseEntity)) {
            throw new RuntimeException("Automatically creation of history is only supported for " + AbstractBaseEntity.class.getName());
        }

        HistoryService<ID> historyService = new HistoryService<>();

        AbstractBaseHistoryEntity<ID> history =
                historyService.buildHistory(((AbstractBaseEntity<ID>) baseEntity));

        entityManager.persist(history);
    }
}
