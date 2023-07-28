package com.bervan.demo.autoconfiguration.model;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.HistoryCollection;
import com.bervan.history.model.HistorySupported;
import com.bervan.ieentities.ExcelIEEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@HistorySupported
public class ProjectTwo implements AbstractBaseEntity<Long>, ExcelIEEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    @ManyToOne
    private UserTwo creator;
    @OneToMany
    @HistoryCollection(historyClass = ProjectHistoryTwo.class)
    private Set<ProjectHistoryTwo> history = new HashSet<>();
    private LocalDateTime modificationDate;

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
