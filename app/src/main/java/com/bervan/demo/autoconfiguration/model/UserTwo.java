package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import javax.persistence.*;

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
public class UserTwo implements AbstractBaseEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String lastName;
    private String nick;

    @OneToMany
    private Set<ProjectTwo> createdProjects = new HashSet<>();
}
