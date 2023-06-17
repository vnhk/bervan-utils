package com.bervan.history.service.case2;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEntityHistoryServiceTest {
    private HistoryService<UUID> historyService;

    @BeforeEach
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