package com.bervan.dtocore;

import com.bervan.dtocore.model.*;
import com.bervan.dtocore.service.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        Author author = Author.builder()
                .id(75L)
                .firstName("Jon")
                .lastName("Smith")
                .build();

        Book book = Book.builder()
                .id(10L)
                .name("Name 1")
                .summary("Summary 1")
                .author(author)
                .secureField("Should not be copied, because DTO has no field with that name")
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((BookDTO) map).getName(), book.getName());
        assertEquals(((BookDTO) map).getSummary(), book.getSummary());

        AuthorDTO authorDTO = ((BookDTO) map).getAuthor();

        assertEquals(authorDTO.getId(), author.getId());
        assertEquals(authorDTO.getFirstName(), author.getFirstName());
        assertEquals(authorDTO.getLastName(), author.getLastName());
    }

    @Test
    public void simpleMapToDTOWithNullableBaseFields() throws Exception {
        Author author = Author.builder()
                .id(75L)
                .firstName(null)
                .lastName("Smith")
                .build();

        Book book = Book.builder()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((BookDTO) map).getName(), book.getName());
        assertEquals(((BookDTO) map).getSummary(), book.getSummary());

        AuthorDTO authorDTO = ((BookDTO) map).getAuthor();

        assertEquals(authorDTO.getId(), author.getId());
        assertEquals(authorDTO.getFirstName(), author.getFirstName());
        assertEquals(authorDTO.getLastName(), author.getLastName());
    }


    @Test
    public void simpleMapToDTOWithNullableInnerDTOFields() throws Exception {
        Author author = null;

        Book book = Book.builder()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((BookDTO) map).getName(), book.getName());
        assertEquals(((BookDTO) map).getSummary(), book.getSummary());

        AuthorDTO authorDTO = ((BookDTO) map).getAuthor();

        assertNull(authorDTO);
    }
}