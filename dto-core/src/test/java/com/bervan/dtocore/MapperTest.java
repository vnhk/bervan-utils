package com.bervan.dtocore;

import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.model.Book;
import com.bervan.dtocore.model.BookDTO;
import com.bervan.dtocore.service.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class MapperTest {
    private DTOMapper dtoMapper;

    @BeforeEach
    public void setUp() {
        dtoMapper = new DTOMapper(new ArrayList<>());
    }

    @Test
    public void simpleMapToDTOWithSkippingField() throws Exception {
        Book book = Book.builder()
                .id(10L)
                .name("Name 1")
                .summary("Summary 1")
                .secureField("Should not be copied, because DTO has no field with that name")
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((BookDTO) map).getName(), book.getName());
        assertEquals(((BookDTO) map).getSummary(), book.getSummary());
    }
}