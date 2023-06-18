package com.bervan.demo.manualconfiguration.repo;

import com.bervan.demo.manualconfiguration.model.UserOne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserOneRepository extends JpaRepository<UserOne, UUID> {

}
