package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import javax.persistence.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTwo implements AbstractBaseEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String lastName;
    private String nick;

    @OneToMany
    private Set<ProjectTwo> createdProjects = new HashSet<>();
}
