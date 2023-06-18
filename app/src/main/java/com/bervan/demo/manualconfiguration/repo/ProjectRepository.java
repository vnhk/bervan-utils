package com.bervan.demo.manualconfiguration.repo;

import com.bervan.demo.manualconfiguration.model.Project;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ProjectRepository extends SimpleJpaRepository<Project, UUID> {
    public ProjectRepository(JpaEntityInformation<Project, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public ProjectRepository(Class<Project> domainClass, EntityManager em) {
        super(domainClass, em);
    }
}
