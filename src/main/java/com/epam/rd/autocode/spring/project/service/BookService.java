package com.epam.rd.autocode.spring.project.service;

import java.util.List;

import com.epam.rd.autocode.spring.project.dto.BookDTO;

public interface BookService {

    List<BookDTO> getAllBooks();

    BookDTO getBookByName(String name);

    BookDTO updateBookByName(String name, BookDTO book);

    void deleteBookByName(String name);

    BookDTO addBook(BookDTO book);
}
