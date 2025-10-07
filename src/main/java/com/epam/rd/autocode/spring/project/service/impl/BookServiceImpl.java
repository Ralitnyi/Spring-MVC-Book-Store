package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	
	private final BookRepository bookRepository;
	private final ModelMapper modelMapper;
	

	public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
		this.bookRepository = bookRepository;
		this.modelMapper = modelMapper;
	}

	@Override
    @Transactional(readOnly = true)
	public List<BookDTO> getAllBooks() {
		List<BookDTO> books = bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .toList();
		return books;
	}

	@Override
    @Transactional(readOnly = true)
	public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name);
        if (book == null) {
            throw new NotFoundException("Book not found: " + name);
        }
        return modelMapper.map(book, BookDTO.class);
	}

	@Override
    @Transactional
	public BookDTO updateBookByName(String name, BookDTO book) {
        Book existing = bookRepository.findByName(name);
        if (existing == null) {
            throw new NotFoundException("Book not found: " + name);
        }
        existing.setName(book.getName());
        existing.setGenre(book.getGenre());
        existing.setAgeGroup(book.getAgeGroup());
        existing.setPrice(book.getPrice());
        existing.setPublicationDate(book.getPublicationDate());
        existing.setAuthor(book.getAuthor());
        existing.setPages(book.getPages());
        existing.setCharacteristics(book.getCharacteristics());
        existing.setDescription(book.getDescription());
        existing.setLanguage(book.getLanguage());
        Book saved = bookRepository.save(existing);
        return modelMapper.map(saved, BookDTO.class);
	}

	@Override
    @Transactional
	public void deleteBookByName(String name) {
        if (!bookRepository.existsByName(name)) {
            throw new NotFoundException("Book not found: " + name);
        }
        bookRepository.deleteByName(name);
	}

	@Override
    @Transactional
	public BookDTO addBook(BookDTO book) {
		bookRepository.save(modelMapper.map(book, Book.class));
		return book;
	}
	
}






