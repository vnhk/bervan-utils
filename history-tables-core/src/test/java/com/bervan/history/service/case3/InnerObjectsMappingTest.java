package com.bervan.history.service.case3;

import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class InnerObjectsMappingTest {
    private HistoryService<Long> historyService;

    @BeforeEach
    public void setUp() {
        historyService = new HistoryService<>();
    }

    @Test
    public void buildHistory() {
        Book book = new Book(1L, "Name A", "Summary A",
                new User(10L, "joedoe", "Joe", "Doe", LocalDateTime.now()), LocalDateTime.now(), new HashSet<>());
        AbstractBaseHistoryEntity<Long> history = historyService.buildHistory(book);

        assertInstanceOf(BookHistory.class, history);
        BookHistory res = (BookHistory) history;

        assertNull(res.getId());

        assertEquals(res.getName(), "Name A");
        assertEquals(res.getSummary(), "Summary A");

        //mapping
        assertEquals(res.getUserNick(), "joedoe");

        assertEquals(res.getEntity(), book);
        assertTrue(book.getHistoryEntities().contains(res));

    }

    @Test
    public void buildHistoryWhenInnerUserIsNull() {
        Book book = new Book(1L, "Name A", "Summary A", null, LocalDateTime.now(), new HashSet<>());
        AbstractBaseHistoryEntity<Long> history = historyService.buildHistory(book);

        assertInstanceOf(BookHistory.class, history);
        BookHistory res = (BookHistory) history;

        assertNull(res.getId());

        assertEquals(res.getName(), "Name A");
        assertEquals(res.getSummary(), "Summary A");

        //mapping
        assertNull(res.getUserNick());

        assertEquals(res.getEntity(), book);
        assertTrue(book.getHistoryEntities().contains(res));

    }
}