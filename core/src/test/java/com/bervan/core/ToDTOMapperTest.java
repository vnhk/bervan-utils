package com.bervan.core;

import com.bervan.core.c1.Author;
import com.bervan.core.c1.AuthorDTO;
import com.bervan.core.c1.Book;
import com.bervan.core.c1.BookDTO;
import com.bervan.core.c2.*;
import com.bervan.core.fieldmapper.*;
import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.BervanLogger;
import com.bervan.core.service.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class ToDTOMapperTest {
    private DTOMapper dtoMapper;
    private BervanLogger log = new BervanLogger() {
        @Override
        public void error(String message) {
            System.out.println(message);
        }

        @Override
        public void info(String message) {
            System.out.println(message);

        }

        @Override
        public void debug(String message) {
            System.out.println(message);

        }

        @Override
        public void warn(String message) {
            System.out.println(message);

        }

        @Override
        public void error(String message, Throwable throwable) {
            System.out.println(message);

        }

        @Override
        public void warn(String message, Throwable throwable) {
            System.out.println(message);
        }

        @Override
        public void error(Throwable throwable) {
            System.out.println(throwable.getMessage());
        }

        @Override
        public void warn(Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    };

    @BeforeEach
    public void setUp() {
        dtoMapper = new DTOMapper(log, new ArrayList<>());
    }

    @Test
    public void simpleMapToDTOWithSkippingField() throws Exception {
        Author author = Author.AuthorBuilder.anAuthor()
                .id(75L)
                .firstName("Jon")
                .lastName("Smith")
                .build();

        Book book = Book.BookBuilder.aBook()
                .id(10L)
                .name("Name 1")
                .summary("Summary 1")
                .author(author)
                .secureField("Should not be copied, because DTO has no field with that name")
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, BookDTO.class);

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
    public void simpleMapToDTOTargetWithSkippingField() throws Exception {
        AuthorDTO author = AuthorDTO.AuthorDTOBuilder.anAuthorDTO()
                .id(75L)
                .firstName("Jon")
                .lastName("Smith")
                .build();

        BookDTO book = BookDTO.BookDTOBuilder.aBookDTO()
                .id(10L)
                .name("Name 1")
                .summary("Summary 1")
                .author(author)
                .anotherSecuredField("Should not be copied, because DTO Target has no field with that name")
                .build();

        BaseModel<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof Book);
        assertEquals(map.getId(), book.getId());
        assertEquals(((Book) map).getName(), book.getName());
        assertEquals(((Book) map).getSummary(), book.getSummary());

        Author authorRes = ((Book) map).getAuthor();

        assertEquals(author.getId(), authorRes.getId());
        assertEquals(author.getFirstName(), authorRes.getFirstName());
        assertEquals(author.getLastName(), authorRes.getLastName());
    }

    @Test
    public void simpleMapToDTOWithNullableBaseFields() throws Exception {
        Author author = Author.AuthorBuilder.anAuthor()
                .id(75L)
                .firstName(null)
                .lastName("Smith")
                .build();

        Book book = Book.BookBuilder.aBook()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, BookDTO.class);

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
    public void simpleMapToDTOTargetWithNullableBaseFields() throws Exception {
        AuthorDTO authorDTO = AuthorDTO.AuthorDTOBuilder.anAuthorDTO()
                .id(75L)
                .firstName(null)
                .lastName("Smith")
                .build();

        BookDTO bookDTO = BookDTO.BookDTOBuilder.aBookDTO()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(authorDTO)
                .anotherSecuredField(null)
                .build();

        BaseModel<Long> map = dtoMapper.map(bookDTO);

        assertTrue(map instanceof Book);
        assertEquals(map.getId(), bookDTO.getId());
        assertEquals(((Book) map).getName(), bookDTO.getName());
        assertEquals(((Book) map).getSummary(), bookDTO.getSummary());

        Author author = ((Book) map).getAuthor();

        assertEquals(authorDTO.getId(), author.getId());
        assertEquals(authorDTO.getFirstName(), author.getFirstName());
        assertEquals(authorDTO.getLastName(), author.getLastName());
    }


    @Test
    public void simpleMapToDTOWithNullableInnerDTOFields() throws Exception {
        Author author = null;

        Book book = Book.BookBuilder.aBook()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, BookDTO.class);

        assertTrue(map instanceof BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((BookDTO) map).getName(), book.getName());
        assertEquals(((BookDTO) map).getSummary(), book.getSummary());

        AuthorDTO authorDTO = ((BookDTO) map).getAuthor();

        assertNull(authorDTO);
    }

    @Test
    public void simpleMapToDTOTargetWithNullableInnerTargetDTOFields() throws Exception {
        AuthorDTO authorDto = null;

        BookDTO bookDto = BookDTO.BookDTOBuilder.aBookDTO()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(authorDto)
                .anotherSecuredField(null)
                .build();

        BaseModel<Long> map = dtoMapper.map(bookDto);

        assertTrue(map instanceof Book);
        assertEquals(map.getId(), bookDto.getId());
        assertEquals(((Book) map).getName(), bookDto.getName());
        assertEquals(((Book) map).getSummary(), bookDto.getSummary());

        Author author = ((Book) map).getAuthor();

        assertNull(author);
    }

    @Test
    public void mapToDTOUsingCustomMapperWhenValueToBeMappedIsNull() throws Exception {
        C2Author author = null;

        dtoMapper = new DTOMapper(log, Arrays.asList(new C2AuthorMapper()));

        C2Book book = C2Book.C2BookBuilder.aC2Book()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, C2BookDTO.class);

        assertTrue(map instanceof C2BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2BookDTO) map).getName(), book.getName());
        assertEquals(((C2BookDTO) map).getSummary(), book.getSummary());

        Long authorId = ((C2BookDTO) map).getAuthor();

        assertNull(authorId);
    }

    @Test
    public void mapToTargetDTOUsingCustomMapperWhenValueToBeMappedIsNull() throws Exception {
        C2AuthorDTO author = null;

        dtoMapper = new DTOMapper(log, Arrays.asList(new C2AuthorDTOMapper()));

        C2BookDTO book = C2BookDTO.C2BookDTOBuilder.aC2BookDTO()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(null)
                .build();

        BaseModel<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C2Book);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2Book) map).getName(), book.getName());
        assertEquals(((C2Book) map).getSummary(), book.getSummary());

        C2Author resAuthor = ((C2Book) map).getAuthor();

        assertNull(resAuthor);
    }

    @Test
    public void mapToTargetDTOUsingCustomMapperWhenValueToBeMapped() throws Exception {
        dtoMapper = new DTOMapper(log, Arrays.asList(new C2AuthorDTOMapper()));

        C2BookDTO book = C2BookDTO.C2BookDTOBuilder.aC2BookDTO()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(152L)
                .build();

        BaseModel<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C2Book);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2Book) map).getName(), book.getName());
        assertEquals(((C2Book) map).getSummary(), book.getSummary());

        C2Author resAuthor = ((C2Book) map).getAuthor();

        assertNotNull(resAuthor);
        assertEquals(resAuthor.getId(), 152L);
    }

    @Test
    public void mapUsingCustomMapperWhenValueToBeMapped() throws Exception {
        C2Author author = C2Author.C2AuthorBuilder.aC2Author().id(150L).firstName("test").lastName("test").build();

        dtoMapper = new DTOMapper(log, Arrays.asList(new C2AuthorMapper()));

        C2Book book = C2Book.C2BookBuilder.aC2Book()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, C2BookDTO.class);

        assertTrue(map instanceof C2BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C2BookDTO) map).getName(), book.getName());
        assertEquals(((C2BookDTO) map).getSummary(), book.getSummary());

        Long authorId = ((C2BookDTO) map).getAuthor();

        assertEquals(authorId, 150L);
    }

    @Test
    public void mapToTargetDTOUsingFieldMapperWithExistingDefaultMapper() throws Exception {
        dtoMapper = new DTOMapper(log, Arrays.asList(new DefaultC3AuthorCustomMapper(), new DefaultC3StringToAuthorCustomMapper()));

        C3BookDTO book = C3BookDTO.C3BookDTOBuilder.aC3BookDTO()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author("John Snow")
                .build();

        BaseModel<Long> map = dtoMapper.map(book);

        assertTrue(map instanceof C3Book);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C3Book) map).getName(), "Name 1");
        assertEquals(((C3Book) map).getSummary(), book.getSummary());

        C3Author authorDetails = ((C3Book) map).getAuthor();

        assertNotNull(authorDetails);
    }

    @Test
    public void mapToDTOUsingFieldMapperWithExistingDefaultMapper() throws Exception {
        C3Author author = C3Author.C3AuthorBuilder.aC3Author().id(150L).firstName("John").lastName("Snow").build();

        dtoMapper = new DTOMapper(log, Arrays.asList(new DefaultC3AuthorCustomMapper(), new DefaultC3StringToAuthorCustomMapper()));

        C3Book book = C3Book.C3BookBuilder.aC3Book()
                .id(null)
                .name("Name 1")
                .summary(null)
                .author(author)
                .secureField(null)
                .build();

        BaseDTO<Long> map = dtoMapper.map(book, C3BookDTO.class);

        assertTrue(map instanceof C3BookDTO);
        assertEquals(map.getId(), book.getId());
        assertEquals(((C3BookDTO) map).getName(), "NAME 1"); //ToUpperCaseMapper
        assertEquals(((C3BookDTO) map).getSummary(), book.getSummary());

        String authorDetails = ((C3BookDTO) map).getAuthor();

        assertEquals(authorDetails, "Author with id = 150: John Snow");
    }
}