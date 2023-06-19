package com.bervan.history.model;

import com.bervan.history.service.HistoryService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Transactional
@Slf4j
public class BaseRepositoryImpl<T extends AbstractBaseEntity<ID>, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public <S extends T> S save(S entity) {
        //if we don't want to have history we should not annotate class with @HistorySupported
        if (!shouldCreateHistory(entity)) {
            return super.save(entity);
        }

        //create
        if (entity.getId() == null) {
            return super.save(entity);
        }

        Optional<T> savedVersion = findById(entity.getId());

        //create with given id
        if (!savedVersion.isPresent()) {
            return super.save(entity);
        }

        //edit
        HistoryService<ID> historyService = new HistoryService<>();
        AbstractBaseHistoryEntity<ID> history = historyService.buildHistory(savedVersion.get());
        entityManager.persist(history);

        return super.save(entity);
    }

    private <S extends T> boolean shouldCreateHistory(S entity) {
        HistorySupported annotation = entity.getClass().getAnnotation(HistorySupported.class);

        if (annotation == null) {
            if (log.isDebugEnabled()) {
                log.debug("History will not be created for entity: " + entity.getClass().getName()
                        + ", because entity is not annotated with " + HistorySupported.class.getName());
            }
            return false;
        }

        return true;
    }

    @Override
    public <S extends T> S saveWithoutHistory(S entity) {
        return super.save(entity);
    }
}
