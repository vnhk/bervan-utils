package com.bervan.demo.manualconfiguration.model;

import jakarta.persistence.Entity;
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
public class User {
    @Id
    private UUID id;
    private String name;
    private String lastName;
    private String nick;

    @OneToMany
    private Set<Project> createdProjects = new HashSet<>();
}
