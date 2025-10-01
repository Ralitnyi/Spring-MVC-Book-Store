package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
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
	public List<BookDTO> getAllBooks() {
		List<BookDTO> books = bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .toList();
		return books;
	}

	@Override
	public BookDTO getBookByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BookDTO updateBookByName(String name, BookDTO book) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBookByName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BookDTO addBook(BookDTO book) {
		bookRepository.save(modelMapper.map(book, Book.class));
		return book;
	}
	
}






