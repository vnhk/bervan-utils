package com.bervan.demo.manualconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.HistorySupported;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOne implements AbstractBaseEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String lastName;
    private String nick;

    @OneToMany
    private Set<ProjectOne> createdProjects = new HashSet<>();
}