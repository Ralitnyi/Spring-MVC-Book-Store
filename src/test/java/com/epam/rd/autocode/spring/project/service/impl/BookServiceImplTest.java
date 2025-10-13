package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.BusinessLogicException;
import com.epam.rd.autocode.spring.project.exception.DatabaseException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.exception.ValidationException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookItemRepository;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookItemRepository bookItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setPages(300);
        book.setPublicationDate(LocalDate.now());
        book.setAgeGroup(AgeGroup.ADULT);
        book.setLanguage(Language.ENGLISH);

        bookDTO = new BookDTO(
            "Test Book",
            "Fiction",
            AgeGroup.ADULT,
            BigDecimal.valueOf(29.99),
            LocalDate.now(),
            "Test Author",
            300,
            null,
            null,
            Language.ENGLISH
        );
    }

    @Test
    void testGetAllBooks_Success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        verify(businessLoggingService).logDataAccess("GET_ALL_BOOKS", "Book", true);
        verify(businessLoggingService).logBusinessEvent("BOOKS_RETRIEVED", "Retrieved 1 books", "system");
    }

    @Test
    void testGetAllBooks_DatabaseException() {
        when(bookRepository.findAll()).thenThrow(new DataAccessException("DB error") {});

        assertThrows(DatabaseException.class, () -> bookService.getAllBooks());

        verify(errorLoggingService).logDatabaseError(
                eq("GET_ALL_BOOKS"),
                any(DataAccessException.class),
                eq("system")
        );
        verify(businessLoggingService).logDataAccess("GET_ALL_BOOKS", "Book", false);
    }


    @Test
    void testGetBooks_WithoutKeyword() {
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getBooks("", 0, 10, "name", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals(bookDTO, result.getContent().get(0));
    }

    @Test
    void testGetBooks_WithKeyword() {
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findByNameContainingIgnoreCase(eq("Test"), any(Pageable.class)))
                .thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getBooks("Test", 0, 10, "name", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals(bookDTO, result.getContent().get(0));
    }

    @Test
    void testGetBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookByName("Test Book");

        assertEquals(bookDTO, result);
    }

    @Test
    void testGetBookByName_NullName() {
        assertThrows(ValidationException.class, () -> bookService.getBookByName(null));
    }

    @Test
    void testGetBookByName_EmptyName() {
        assertThrows(ValidationException.class, () -> bookService.getBookByName(""));
    }

    @Test
    void testGetBookByName_NotFound() {
        when(bookRepository.findByName("Nonexistent")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.getBookByName("Nonexistent"));
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookById(1L);

        assertEquals(bookDTO, result);
    }

    @Test
    void testGetBookById_InvalidId() {
        assertThrows(ValidationException.class, () -> bookService.getBookById(null));
        assertThrows(ValidationException.class, () -> bookService.getBookById(0L));
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(999L));
    }

    @Test
    void testUpdateBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.updateBookByName("Test Book", bookDTO);

        assertEquals(bookDTO, result);
        verify(bookRepository).save(book);
    }

    @Test
    void testUpdateBookByName_NotFound() {
        when(bookRepository.findByName("Nonexistent")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.updateBookByName("Nonexistent", bookDTO));
    }

    @Test
    void testUpdateBookByName_AlreadyExists() {
        BookDTO newBookDTO = new BookDTO();
        newBookDTO.setName("Existing Book");

        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(bookRepository.existsByName("Existing Book")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> bookService.updateBookByName("Test Book", newBookDTO));
    }

    @Test
    void testUpdateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.updateBook(1L, bookDTO);

        assertEquals(bookDTO, result);
        verify(bookRepository).save(book);
    }

    @Test
    void testDeleteBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(bookItemRepository.findByBook(book)).thenReturn(List.of());

        bookService.deleteBookByName("Test Book");

        verify(bookRepository).deleteByName("Test Book");
    }

    @Test
    void testDeleteBookByName_WithBookItems() {
        BookItem bookItem = new BookItem();
        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(bookItemRepository.findByBook(book)).thenReturn(List.of(bookItem));

        bookService.deleteBookByName("Test Book");

        verify(bookItemRepository).deleteAll(List.of(bookItem));
        verify(bookRepository).deleteByName("Test Book");
    }

    @Test
    void testDeleteBookByName_NotFound() {
        when(bookRepository.findByName("Nonexistent")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.deleteBookByName("Nonexistent"));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookItemRepository.findByBook(book)).thenReturn(List.of());

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void testAddBook_Success() {
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setName("Test Book");

        when(bookRepository.existsByName("Test Book")).thenReturn(false);
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertEquals(bookDTO, result);
        verify(bookRepository).save(book);
    }

    @Test
    void testAddBook_AlreadyExists() {
        when(bookRepository.existsByName("Test Book")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> bookService.addBook(bookDTO));
    }

    @Test
    void testAddBook_NullBook() {
        assertThrows(ValidationException.class, () -> bookService.addBook(null));
    }
}
