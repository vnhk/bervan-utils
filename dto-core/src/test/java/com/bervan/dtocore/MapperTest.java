package com.bervan.dtocore;

import com.bervan.dtocore.c1.Author;
import com.bervan.dtocore.c1.AuthorDTO;
import com.bervan.dtocore.c1.Book;
import com.bervan.dtocore.c1.BookDTO;
import com.bervan.dtocore.c2.C2Author;
import com.bervan.dtocore.c2.C2AuthorMapper;
import com.bervan.dtocore.c2.C2Book;
import com.bervan.dtocore.c2.C2BookDTO;
import com.bervan.dtocore.fieldmapper.C3Author;
import com.bervan.dtocore.fieldmapper.C3Book;
import com.bervan.dtocore.fieldmapper.C3BookDTO;
import com.bervan.dtocore.fieldmapper.DefaultC3AuthorCustomMapper;
import com.bervan.dtocore.model.BaseDTO;
import com.bervan.dtocore.service.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Test
    public void mapUsingCustomMapperWhenValueToBeMappedIsNull() throws Exception {
        C2Author author = null;

        dtoMapper = new DTOMapper(Arrays.asList(new C2AuthorMapper()));

        C2Book book = C2Book.builder()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C2BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2BookDTO) map).getName(), book.getName());
        assertEquals(((C2BookDTO) map).getSummary(), book.getSummary());

        Long authorId = ((C2BookDTO) map).getAuthor();

        assertNull(authorId);
    }

    @Test
    public void mapUsingCustomMapperWhenValueToBeMapped() throws Exception {
        C2Author author = C2Author.builder().id(150L).firstName("test").lastName("test").build();

        dtoMapper = new DTOMapper(Arrays.asList(new C2AuthorMapper()));

        C2Book book = C2Book.builder()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C2BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2BookDTO) map).getName(), book.getName());
        assertEquals(((C2BookDTO) map).getSummary(), book.getSummary());

        Long authorId = ((C2BookDTO) map).getAuthor();

        assertEquals(authorId, 150L);
    }

    @Test
    public void mapUsingFieldMapperWithExistingDefaultMapper() throws Exception {
        C3Author author = C3Author.builder().id(150L).firstName("John").lastName("Snow").build();

        dtoMapper = new DTOMapper(Arrays.asList(new DefaultC3AuthorCustomMapper()));

        C3Book book = C3Book.builder()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C3BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C3BookDTO) map).getName(), "NAME 1"); //ToUpperCaseMapper
        assertEquals(((C3BookDTO) map).getSummary(), book.getSummary());

        String authorDetails = ((C3BookDTO) map).getAuthor();

        assertEquals(authorDetails, "Author with id = 150: John Snow");
    }
}