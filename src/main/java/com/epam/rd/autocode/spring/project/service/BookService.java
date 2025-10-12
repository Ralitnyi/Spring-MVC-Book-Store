package com.epam.rd.autocode.spring.project.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.Book;

public interface BookService {

    List<BookDTO> getAllBooks();

    BookDTO getBookByName(String name);

    BookDTO updateBookByName(String name, BookDTO book);

    void deleteBookByName(String name);

    BookDTO addBook(BookDTO book);

    BookDTO getBookById(Long id);

    BookDTO updateBook(Long id, BookDTO book);

    void deleteBook(Long id);

	Page<BookDTO> getBooks(String keyword, int page, int size, String sortField, String sortDir);
}