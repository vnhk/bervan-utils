package com.bervan.demo.manualconfiguration.repo;

import com.bervan.demo.manualconfiguration.model.User;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepository extends SimpleJpaRepository<User, UUID> {
    public UserRepository(JpaEntityInformation<User, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public UserRepository(Class<User> domainClass, EntityManager em) {
        super(domainClass, em);
    }
}
