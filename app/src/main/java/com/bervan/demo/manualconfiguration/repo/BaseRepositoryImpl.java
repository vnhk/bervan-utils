package com.bervan.demo.manualconfiguration.repo;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Transactional
public class BaseRepositoryImpl<T extends AbstractBaseEntity<ID>, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public T saveWithHistory(T entity) {
        if (entity == null) {
            throw new RuntimeException("Automatically creation of history is only supported for not nullable " + AbstractBaseEntity.class.getName());
        }

        if (entity.getId() == null) {
            return super.save(entity);
        }

        T savedVersion = findById(entity.getId()).get();

        HistoryService<ID> historyService = new HistoryService<>();
        AbstractBaseHistoryEntity<ID> history = historyService.buildHistory(savedVersion);
        entityManager.persist(history);

        return super.save(entity);
    }
}
