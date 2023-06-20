package com.bervan.history.service.case1;

import com.bervan.history.model.AbstractBaseEntity;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import com.bervan.history.model.HistoryField;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookHistory implements AbstractBaseHistoryEntity<Long> {
    //project history id if want to save it to the database
    private Long id;
    @HistoryField
    private String name;
    @HistoryField
    private String summary;

    //relation
    private Book book;

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