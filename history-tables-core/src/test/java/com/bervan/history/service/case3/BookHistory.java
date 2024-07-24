package com.bervan.history.service.case3;

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
    @HistoryField(savePath = "user.nick")
    private String userNick;

    public BookHistory() {

    }

    //relation
    private Book book;
    private LocalDateTime updateDate;

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getUserNick() {
        return userNick;
    }

    public Book getBook() {
        return book;
    }

    public BookHistory(Long id, String name, String summary, String userNick, Book book, LocalDateTime updateDate) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.userNick = userNick;
        this.book = book;
        this.updateDate = updateDate;
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
}
