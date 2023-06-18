package com.bervan.demo.manualconfiguration.repo;

import com.bervan.demo.manualconfiguration.model.ProjectHistory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ProjectHistoryRepository extends SimpleJpaRepository<ProjectHistory, UUID> {
    public ProjectHistoryRepository(JpaEntityInformation<ProjectHistory, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public ProjectHistoryRepository(Class<ProjectHistory> domainClass, EntityManager em) {
        super(domainClass, em);
    }
}
