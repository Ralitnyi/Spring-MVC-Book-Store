package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.BusinessLogicException;
import com.epam.rd.autocode.spring.project.exception.DatabaseException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.exception.ValidationException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.repo.BookItemRepository;

@Service
public class BookServiceImpl implements BookService {
	
	private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
	private final BookRepository bookRepository;
	private final BookItemRepository bookItemRepository;
	private final ModelMapper modelMapper;
	private final BusinessLoggingService businessLoggingService;
	private final ErrorLoggingService errorLoggingService;
	

	public BookServiceImpl(BookRepository bookRepository, BookItemRepository bookItemRepository, ModelMapper modelMapper,
	                      BusinessLoggingService businessLoggingService,
	                      ErrorLoggingService errorLoggingService) {
		this.bookRepository = bookRepository;
		this.bookItemRepository = bookItemRepository;
		this.modelMapper = modelMapper;
		this.businessLoggingService = businessLoggingService;
		this.errorLoggingService = errorLoggingService;
	}

	@Override
    @Transactional(readOnly = true)
	public List<BookDTO> getAllBooks() {
		try {
			logger.debug("Retrieving all books");
			businessLoggingService.logDataAccess("GET_ALL_BOOKS", "Book", true);
			List<BookDTO> books = bookRepository.findAll()
	                .stream()
	                .map(book -> modelMapper.map(book, BookDTO.class))
	                .toList();
			logger.debug("Successfully retrieved {} books", books.size());
			businessLoggingService.logBusinessEvent("BOOKS_RETRIEVED", "Retrieved " + books.size() + " books", "system");
			return books;
		} catch (DataAccessException ex) {
			logger.error("Database error while retrieving all books", ex);
			errorLoggingService.logDatabaseError("GET_ALL_BOOKS", ex, "system");
			businessLoggingService.logDataAccess("GET_ALL_BOOKS", "Book", false);
			throw new DatabaseException("Failed to retrieve books from database", ex);
		} catch (Exception ex) {
			logger.error("Unexpected error while retrieving all books", ex);
			errorLoggingService.logApplicationError("GET_ALL_BOOKS", ex, "system");
			businessLoggingService.logDataAccess("GET_ALL_BOOKS", "Book", false);
			throw new BusinessLogicException("Failed to retrieve books", ex);
		}
	}

	@Override
    @Transactional(readOnly = true)
	public BookDTO getBookByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			logger.warn("Attempt to get book with null or empty name");
			throw new ValidationException("Book name cannot be null or empty");
		}
		
		try {
			logger.debug("Retrieving book by name: {}", name);
			Book book = bookRepository.findByName(name);
			if (book == null) {
				logger.warn("Book not found with name: {}", name);
				throw new NotFoundException("Book not found: " + name);
			}
			logger.debug("Successfully retrieved book: {}", name);
			return modelMapper.map(book, BookDTO.class);
		} catch (DataAccessException ex) {
			logger.error("Database error while retrieving book by name: {}", name, ex);
			throw new DatabaseException("Failed to retrieve book from database", ex);
		} catch (NotFoundException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (ValidationException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (Exception ex) {
			logger.error("Unexpected error while retrieving book by name: {}", name, ex);
			throw new BusinessLogicException("Failed to retrieve book", ex);
		}
	}

	@Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
		if (id == null || id <= 0) {
			logger.warn("Attempt to get book with invalid id: {}", id);
			throw new ValidationException("Book ID must be a positive number");
		}
		
		try {
			logger.debug("Retrieving book by id: {}", id);
			Book book = bookRepository.findById(id)
				.orElseThrow(() -> {
					logger.warn("Book not found with id: {}", id);
					return new NotFoundException("Book not found: " + id);
				});
			logger.debug("Successfully retrieved book by id: {}", id);
			return modelMapper.map(book, BookDTO.class);
		} catch (DataAccessException ex) {
			logger.error("Database error while retrieving book by id: {}", id, ex);
			throw new DatabaseException("Failed to retrieve book from database", ex);
		} catch (NotFoundException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (ValidationException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (Exception ex) {
			logger.error("Unexpected error while retrieving book by id: {}", id, ex);
			throw new BusinessLogicException("Failed to retrieve book", ex);
		}
    }

	@Override
    @Transactional
	public BookDTO updateBookByName(String name, BookDTO book) {
		if (name == null || name.trim().isEmpty()) {
			logger.warn("Attempt to update book with null or empty name");
			throw new ValidationException("Book name cannot be null or empty");
		}
		if (book == null) {
			logger.warn("Attempt to update book with null book data");
			throw new ValidationException("Book data cannot be null");
		}
		
		try {
			logger.debug("Updating book by name: {}", name);
			Book existing = bookRepository.findByName(name);
			if (existing == null) {
				logger.warn("Book not found for update with name: {}", name);
				throw new NotFoundException("Book not found: " + name);
			}
			
			// Check if new name already exists (if name is being changed)
			if (!existing.getName().equals(book.getName()) && bookRepository.existsByName(book.getName())) {
				logger.warn("Book with name {} already exists", book.getName());
				throw new AlreadyExistException("Book with name '" + book.getName() + "' already exists");
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
			logger.debug("Successfully updated book: {}", name);
			return modelMapper.map(saved, BookDTO.class);
		} catch (DataAccessException ex) {
			logger.error("Database error while updating book by name: {}", name, ex);
			throw new DatabaseException("Failed to update book in database", ex);
		} catch (NotFoundException | AlreadyExistException | ValidationException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (Exception ex) {
			logger.error("Unexpected error while updating book by name: {}", name, ex);
			throw new BusinessLogicException("Failed to update book", ex);
		}
	}

    @Override
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found: " + id));
        book.setName(bookDTO.getName());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());
        book.setLanguage(bookDTO.getLanguage());
        book.setAgeGroup(bookDTO.getAgeGroup());
        bookRepository.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

	@Override
    @Transactional
	public void deleteBookByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			logger.warn("Attempt to delete book with null or empty name");
			throw new ValidationException("Book name cannot be null or empty");
		}
		
		try {
			logger.debug("Deleting book by name: {}", name);
			Book book = bookRepository.findByName(name);
			if (book == null) {
				logger.warn("Book not found for deletion with name: {}", name);
				throw new NotFoundException("Book not found: " + name);
			}
			
			// First delete all BookItem records that reference this book
			logger.debug("Deleting all BookItem records for book: {}", name);
			List<BookItem> bookItems = bookItemRepository.findByBook(book);
			if (!bookItems.isEmpty()) {
				logger.info("Found {} BookItem records to delete for book: {}", bookItems.size(), name);
				bookItemRepository.deleteAll(bookItems);
				logger.info("Successfully deleted {} BookItem records for book: {}", bookItems.size(), name);
			} else {
				logger.debug("No BookItem records found for book: {}", name);
			}
			
			// Then delete the book itself
			bookRepository.deleteByName(name);
			logger.debug("Successfully deleted book: {}", name);
		} catch (DataAccessException ex) {
			logger.error("Database error while deleting book by name: {}", name, ex);
			throw new DatabaseException("Failed to delete book from database", ex);
		} catch (NotFoundException | ValidationException ex) {
			throw ex; // Re-throw custom exceptions
		} catch (Exception ex) {
			logger.error("Unexpected error while deleting book by name: {}", name, ex);
			throw new BusinessLogicException("Failed to delete book", ex);
		}
	}

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found: " + id));
        
        // First delete all BookItem records that reference this book
        logger.debug("Deleting all BookItem records for book ID: {}", id);
        List<BookItem> bookItems = bookItemRepository.findByBook(book);
        if (!bookItems.isEmpty()) {
            logger.debug("Found {} BookItem records to delete for book ID: {}", bookItems.size(), id);
            bookItemRepository.deleteAll(bookItems);
        }
        
        // Then delete the book itself
        bookRepository.delete(book);
        logger.debug("Successfully deleted book with ID: {}", id);
    }

	@Override
    @Transactional
	public BookDTO addBook(BookDTO book) {
		if (book == null) {
			logger.warn("Attempt to add null book");
			throw new ValidationException("Book data cannot be null");
		}
		if (book.getName() == null || book.getName().trim().isEmpty()) {
			logger.warn("Attempt to add book with null or empty name");
			throw new ValidationException("Book name cannot be null or empty");
		}
		
		try {
			logger.debug("Adding new book: {}", book.getName());
			if (bookRepository.existsByName(book.getName())) {
				logger.warn("Book with name {} already exists", book.getName());
				throw new AlreadyExistException("Book with name '" + book.getName() + "' already exists");
			}
			
			Book bookEntity = modelMapper.map(book, Book.class);
			Book saved = bookRepository.save(bookEntity);
			logger.debug("Successfully added book: {}", book.getName());
			return modelMapper.map(saved, BookDTO.class);
		} catch (DataAccessException ex) {
			logger.error("Database error while adding book: {}", book.getName(), ex);
			throw new DatabaseException("Failed to add book to database", ex);
		} catch (AlreadyExistException | ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Unexpected error while adding book: {}", book.getName(), ex);
			throw new BusinessLogicException("Failed to add book", ex);
		}
	}
	
}