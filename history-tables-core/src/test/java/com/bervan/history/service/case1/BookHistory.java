package com.bervan.history.service.case1;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;

import java.time.LocalDateTime;

public class BookHistory implements AbstractBaseHistoryEntity<Long> {
    //project history id if want to save it to the database
    private Long id;
    @HistoryField
    private String name;
    @HistoryField
    private String summary;

    //relation
    private Book book;
    private LocalDateTime updateDate;

    public BookHistory(Long id, String name, String summary, Book book, LocalDateTime updateDate) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.book = book;
        this.updateDate = updateDate;
    }

    public BookHistory() {

    }

    @Override
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public void setEntity(AbstractBaseEntity<Long> entity) {
        this.book = (Book) entity;
    }

    @Override
    public AbstractBaseEntity<Long> getEntity() {
        return book;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

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
}
