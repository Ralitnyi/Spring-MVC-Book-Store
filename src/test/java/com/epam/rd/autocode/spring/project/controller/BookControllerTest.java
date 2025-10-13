package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookServiceImpl bookService;

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private BookController bookController;

    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO();
        bookDTO.setName("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setGenre("Fiction");
        bookDTO.setPrice(BigDecimal.valueOf(19.99));
        bookDTO.setPublicationDate(LocalDate.now());
        bookDTO.setPages(300);
        bookDTO.setAgeGroup(AgeGroup.ADULT);
        bookDTO.setLanguage(Language.ENGLISH);
    }

    @Test
    void testListBooks_Success() {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookService.getAllBooks()).thenReturn(books);

        String result = bookController.listBooks(model);

        assertEquals("book/books", result);
        verify(model).addAttribute("books", books);
        verify(businessLoggingService).logBusinessEvent(eq("BOOK_LIST_REQUESTED"), anyString(), eq("anonymous"));
    }

    @Test
    void testListBooks_Exception() {
        when(bookService.getAllBooks()).thenThrow(new RuntimeException("Test exception"));

        String result = bookController.listBooks(model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("LIST_BOOKS"), any(Exception.class), eq("anonymous"));
    }

    @Test
    void testBooksList2_Success() {
        List<BookDTO> books = Arrays.asList(bookDTO);
        Page<BookDTO> bookPage = new PageImpl<>(books);
        when(bookService.getBooks(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(bookPage);

        String result = bookController.booksList2("test", 0, 9, "name", "asc", model);

        assertEquals("book/list", result);
        verify(model).addAttribute("bookPage", bookPage);
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", bookPage.getTotalPages());
        verify(model).addAttribute("sortField", "name");
        verify(model).addAttribute("sortDir", "asc");
        verify(model).addAttribute("reverseSortDir", "desc");
        verify(model).addAttribute("keyword", "test");
    }

    @Test
    void testShowAddBookForm() {
        String result = bookController.showAddBookForm(model);

        assertEquals("book/add-book", result);
        verify(model).addAttribute(eq("book"), any(BookDTO.class));
        verify(model).addAttribute("ageGroups", AgeGroup.values());
        verify(model).addAttribute("languages", Language.values());
    }

    @Test
    void testAddBook_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = bookController.addBook(bookDTO, bindingResult, model);

        assertEquals("redirect:/books", result);
        verify(bookService).addBook(bookDTO);
        verify(businessLoggingService).logBookCreated(bookDTO.getName(), "anonymous");
    }

    @Test
    void testAddBook_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = bookController.addBook(bookDTO, bindingResult, model);

        assertEquals("book/add-book", result);
        verify(bookService, never()).addBook(any());
        verify(errorLoggingService).logValidationError(eq("book"), anyString(), eq("Validation failed"), eq("anonymous"));
        verify(model).addAttribute("ageGroups", AgeGroup.values());
        verify(model).addAttribute("languages", Language.values());
    }

    @Test
    void testAddBook_Exception() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Test exception")).when(bookService).addBook(any(BookDTO.class));

        String result = bookController.addBook(bookDTO, bindingResult, model);

        assertEquals("book/add-book", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("ADD_BOOK"), any(Exception.class), eq("anonymous"));
    }

    @Test
    void testViewBook_Success() {
        String bookName = "Test Book";
        when(bookService.getBookByName(bookName)).thenReturn(bookDTO);

        String result = bookController.viewBook(bookName, model);

        assertEquals("book/book-detail", result);
        verify(model).addAttribute("book", bookDTO);
        verify(businessLoggingService).logBookViewed(bookName, "anonymous");
    }

    @Test
    void testViewBook_Exception() {
        String bookName = "Test Book";
        when(bookService.getBookByName(bookName)).thenThrow(new NotFoundException("Book not found"));

        String result = bookController.viewBook(bookName, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("VIEW_BOOK"), any(Exception.class), eq("anonymous"));
    }

    @Test
    void testEditBookForm_Success() {
        String bookName = "Test Book";
        when(bookService.getBookByName(bookName)).thenReturn(bookDTO);

        String result = bookController.editBookForm(bookName, model);

        assertEquals("book/edit-book", result);
        verify(model).addAttribute("book", bookDTO);
        verify(model).addAttribute("originalName", bookName);
        verify(model).addAttribute("ageGroups", AgeGroup.values());
        verify(model).addAttribute("languages", Language.values());
    }

    @Test
    void testEditBookForm_Exception() {
        String bookName = "Test Book";
        when(bookService.getBookByName(bookName)).thenThrow(new NotFoundException("Book not found"));

        String result = bookController.editBookForm(bookName, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void testUpdateBook_Success() {
        String bookName = "Test Book";
        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookService.updateBookByName(bookName, bookDTO)).thenReturn(bookDTO);

        String result = bookController.updateBook(bookName, bookDTO, bindingResult, model);

        assertEquals("redirect:/books/" + bookDTO.getName(), result);
        verify(bookService).updateBookByName(bookName, bookDTO);
        verify(businessLoggingService).logBookUpdated(bookDTO.getName(), "anonymous");
    }

    @Test
    void testUpdateBook_ValidationErrors() {
        String bookName = "Test Book";
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = bookController.updateBook(bookName, bookDTO, bindingResult, model);

        assertEquals("book/edit-book", result);
        verify(bookService, never()).updateBookByName(anyString(), any());
        verify(errorLoggingService).logValidationError(eq("book"), anyString(), eq("Validation failed"), eq("anonymous"));
        verify(model).addAttribute("originalName", bookName);
    }

    @Test
    void testUpdateBook_Exception() {
        String bookName = "Test Book";
        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookService.updateBookByName(anyString(), any(BookDTO.class)))
            .thenThrow(new RuntimeException("Test exception"));

        String result = bookController.updateBook(bookName, bookDTO, bindingResult, model);

        assertEquals("book/edit-book", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("UPDATE_BOOK"), any(Exception.class), eq("anonymous"));
    }

    @Test
    void testDeleteBook_Success() {
        String bookName = "Test Book";

        String result = bookController.deleteBook(bookName, model);

        assertEquals("redirect:/books", result);
        verify(bookService).deleteBookByName(bookName);
        verify(businessLoggingService).logBookDeleted(bookName, "anonymous");
    }

    @Test
    void testDeleteBook_Exception() {
        String bookName = "Test Book";
        doThrow(new RuntimeException("Test exception")).when(bookService).deleteBookByName(bookName);

        String result = bookController.deleteBook(bookName, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("DELETE_BOOK"), any(Exception.class), eq("anonymous"));
    }
}
