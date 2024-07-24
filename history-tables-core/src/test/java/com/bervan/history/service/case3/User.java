package com.bervan.history.service.case3;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public class User implements AbstractBaseEntity<Long> {
    private Long id;
    private String nick;
    private String name;
    private String surname;
    private LocalDateTime modificationDate;

    public User(Long id, String nick, String name, String surname, LocalDateTime modificationDate) {
        this.id = id;
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.modificationDate = modificationDate;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public Set<? extends AbstractBaseHistoryEntity<Long>> getHistoryEntities() {
        return null;
    }

    @Override
    public void setHistoryEntities(Collection<? extends AbstractBaseHistoryEntity<Long>> abstractBaseHistoryEntities) {

    }

    @Override
    public Class<? extends AbstractBaseHistoryEntity<Long>> getTargetHistoryEntityClass() {
        return null;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
