package com.bervan.history.service.autoconfig.case2;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AutoConfigSimpleEntityHistoryServiceTest {
    private HistoryService<UUID> historyService;

    @BeforeEach
    public void setUp() {
        historyService = new HistoryService<>();
    }

    @Test
    public void buildHistory() {
        UUID uuid = UUID.randomUUID();

        Project moodleProject = new Project(uuid, "Moodle"
                , "Half-Free Web Learning System"
                , "This project is about creating web application that supports remote learning.",
                BigDecimal.valueOf(150),
                5,
                ProjectStatus.IN_PROGRESS);

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
        Project moodleProject =new Project();
        moodleProject.setSummary("Half-Free Web Learning System");

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