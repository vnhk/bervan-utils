package com.bervan.history.service.case3;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public class Book implements AbstractBaseEntity<Long> {

    private Long id;
    private String name;
    private String summary;
    private User user;
    private LocalDateTime modificationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<BookHistory> getHistory() {
        return history;
    }

    public void setHistory(Set<BookHistory> history) {
        this.history = history;
    }

    public Book(Long id, String name, String summary, User user, LocalDateTime modificationDate, Set<BookHistory> history) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.user = user;
        this.modificationDate = modificationDate;
        this.history = history;
    }

    public Book() {

    }

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    //history
    private Set<BookHistory> history;

    @Override
    public Set<? extends AbstractBaseHistoryEntity<Long>> getHistoryEntities() {
        return history;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<Long>> historyEntities) {
        this.history = (Set<BookHistory>) historyEntities;
    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<Long>> getTargetHistoryEntityClass() {
        return BookHistory.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
