package com.bervan.entities;

import lombok.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

enum ProjectStatus {
    OPEN, IN_PROGRESS, CLOSED, CANCELED, DONE
}

@Getter
@Setter
@Builder
class Project implements AbstractBaseEntity<UUID> {

    private UUID id;
    private String name;
    private String summary;
    private String description;
    private BigDecimal price;
    private Integer importance;
    private ProjectStatus status;

    //history
    private Set<ProjectHistory> history;

    @Override
    public Set<? extends AbstractBaseHistoryEntity<UUID>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Set<? extends AbstractBaseHistoryEntity<UUID>> historyEntities) {
        this.history = (Set<ProjectHistory>) historyEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<UUID>> getTargetHistoryEntityClass() {
        return ProjectHistory.class;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ProjectHistory implements AbstractBaseHistoryEntity<UUID> {
    //project history id if want to save it to the database
    private UUID id;
    @HistoryField
    private String name;
    @HistoryField
    private String summary;

    //decided to not have history for description, can be removed from ProjectHistory
    private String description;

    @HistoryField
    private BigDecimal price;
    @HistoryField
    private Integer importance;
    @HistoryField
    private ProjectStatus status;

    //relation
    private Project project;

    @Override
    public void setEntity(AbstractBaseEntity<UUID> entity) {
        this.project = (Project) entity;
    }

    @Override
    public AbstractBaseEntity<UUID> getEntity() {
        return project;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}

public class SimpleEntityHistoryServiceTest {
    private HistoryService<UUID> historyService;

    @Before
    public void setUp() {
        historyService = new HistoryService<>();
    }

    @Test
    public void buildHistory() {
        UUID uuid = UUID.randomUUID();

        Project moodleProject = Project.builder()
                .name("Moodle")
                .summary("Half-Free Web Learning System")
                .description("This project is about creating web application that supports remote learning.")
                .price(BigDecimal.valueOf(150))
                .status(ProjectStatus.IN_PROGRESS)
                .importance(5)
                .id(uuid)
                .build();

        AbstractBaseHistoryEntity<UUID> history =
                historyService.buildHistory(moodleProject);

        assertInstanceOf(ProjectHistory.class, history);
        ProjectHistory res = (ProjectHistory) history;

        //id and description null
        assertNull(res.getDescription());
        assertNull(res.getId());

        assertEquals(res.getImportance(), moodleProject.getImportance());
        assertEquals(res.getName(), moodleProject.getName());
        assertEquals(res.getStatus(), moodleProject.getStatus());
        assertEquals(res.getSummary(), moodleProject.getSummary());
        assertEquals(res.getPrice(), moodleProject.getPrice());

        assertEquals(res.getProject(), moodleProject);
        assertTrue(moodleProject.getHistoryEntities().contains(res));
    }

    @Test
    public void buildHistoryNullableSimpleAttributes() {
        Project moodleProject = Project.builder()
                .name(null)
                .summary("Half-Free Web Learning System")
                .description(null)
                .price(null)
                .status(null)
                .importance(null)
                .id(null)
                .build();

        AbstractBaseHistoryEntity<UUID> history =
                historyService.buildHistory(moodleProject);

        assertInstanceOf(ProjectHistory.class, history);
        ProjectHistory res = (ProjectHistory) history;

        //id and description null
        assertNull(res.getDescription());
        assertNull(res.getId());

        assertEquals(res.getImportance(), moodleProject.getImportance());
        assertEquals(res.getName(), moodleProject.getName());
        assertEquals(res.getStatus(), moodleProject.getStatus());
        assertEquals(res.getSummary(), moodleProject.getSummary());
        assertEquals(res.getPrice(), moodleProject.getPrice());

        assertEquals(res.getProject(), moodleProject);
        assertTrue(moodleProject.getHistoryEntities().contains(res));
    }
}