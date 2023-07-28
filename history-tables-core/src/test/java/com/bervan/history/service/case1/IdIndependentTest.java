package com.bervan.history.service.case1;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class IdIndependentTest {
    private HistoryService<Long> historyService;

    @BeforeEach
    public void setUp() {
        historyService = new HistoryService<>();
    }

    @Test
    public void buildHistory() {
        Book book = new Book(1L, "Name A", "Summary A", LocalDateTime.now(), new HashSet<>());
        AbstractBaseHistoryEntity<Long> history = historyService.buildHistory(book);

        assertInstanceOf(BookHistory.class, history);
        BookHistory res = (BookHistory) history;

        assertNull(res.getId());

        assertEquals(res.getName(), "Name A");
        assertEquals(res.getSummary(), "Summary A");

        assertEquals(res.getEntity(), book);
        assertTrue(book.getHistoryEntities().contains(res));

    }
}