package com.bervan.demo.manualconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOne implements AbstractBaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private String nick;

    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
    private Set<ProjectOne> createdProjects = new HashSet<>();
}
