package com.epam.rd.autocode.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/books")
public class BookController {

	private final BookServiceImpl bookService;
	private final BusinessLoggingService businessLoggingService;
	private final ErrorLoggingService errorLoggingService;

	public BookController(BookServiceImpl bookService, 
	                     BusinessLoggingService businessLoggingService,
	                     ErrorLoggingService errorLoggingService) {
		this.bookService = bookService;
		this.businessLoggingService = businessLoggingService;
		this.errorLoggingService = errorLoggingService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('CLIENT', 'EMPLOYEE', 'ADMIN')")
	public String listBooks(Model model) {
		try {
			businessLoggingService.logBusinessEvent("BOOK_LIST_REQUESTED", "User requested book list", "anonymous");
			model.addAttribute("books", bookService.getAllBooks());
			return "book/books";
		} catch (Exception ex) {
			errorLoggingService.logApplicationError("LIST_BOOKS", ex, "anonymous");
			model.addAttribute("errorMessage", "Failed to load books: " + ex.getMessage());
			return "error/error";
		}
	}

	@GetMapping("/add")
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	public String showAddBookForm(Model model) {
		model.addAttribute("book", new BookDTO());
		model.addAttribute("ageGroups", AgeGroup.values());
		model.addAttribute("languages", Language.values());
		return "book/add-book";
	}

	@PostMapping("/add")
//	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	public String addBook(@ModelAttribute("book") @Valid BookDTO bookDTO,
			BindingResult bindingResult,
			Model model) {
		try {
			if (bindingResult.hasErrors()) {
				errorLoggingService.logValidationError("book", bookDTO.toString(), "Validation failed", "anonymous");
				model.addAttribute("ageGroups", AgeGroup.values());
				model.addAttribute("languages", Language.values());
				return "book/add-book";
			}
			bookService.addBook(bookDTO);
			businessLoggingService.logBookCreated(bookDTO.getName(), "anonymous");
			return "redirect:/books";
		} catch (Exception ex) {
			errorLoggingService.logApplicationError("ADD_BOOK", ex, "anonymous");
			model.addAttribute("errorMessage", "Failed to add book: " + ex.getMessage());
			model.addAttribute("ageGroups", AgeGroup.values());
			model.addAttribute("languages", Language.values());
			return "book/add-book";
		}
	}

	@GetMapping("/{name}")
	public String viewBook(@PathVariable("name") String name, Model model) {
		try {
			businessLoggingService.logBookViewed(name, "anonymous");
			model.addAttribute("book", bookService.getBookByName(name));
			return "book/book-detail";
		} catch (Exception ex) {
			errorLoggingService.logApplicationError("VIEW_BOOK", ex, "anonymous");
			model.addAttribute("errorMessage", "Failed to load book details: " + ex.getMessage());
			return "error/error";
		}
	}

	@GetMapping("/{name}/edit")
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	public String editBookForm(@PathVariable("name") String name, Model model) {
		try {
			model.addAttribute("book", bookService.getBookByName(name));
			model.addAttribute("originalName", name);
			model.addAttribute("ageGroups", AgeGroup.values());
			model.addAttribute("languages", Language.values());
			return "book/edit-book";
		} catch (Exception ex) {
			model.addAttribute("errorMessage", "Failed to load book for editing: " + ex.getMessage());
			return "error/error";
		}
	}

	@PostMapping("/{name}/edit")
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	public String updateBook(@PathVariable("name") String name, @ModelAttribute("book") @Valid BookDTO bookDTO,
			BindingResult bindingResult,
			Model model) {
		try {
			if (bindingResult.hasErrors()) {
				errorLoggingService.logValidationError("book", bookDTO.toString(), "Validation failed", "anonymous");
				model.addAttribute("originalName", name);
				model.addAttribute("ageGroups", AgeGroup.values());
				model.addAttribute("languages", Language.values());
				return "book/edit-book";
			}
			bookService.updateBookByName(name, bookDTO);
			businessLoggingService.logBookUpdated(bookDTO.getName(), "anonymous");
			return "redirect:/books/" + bookDTO.getName();
		} catch (Exception ex) {
			errorLoggingService.logApplicationError("UPDATE_BOOK", ex, "anonymous");
			model.addAttribute("errorMessage", "Failed to update book: " + ex.getMessage());
			model.addAttribute("originalName", name);
			model.addAttribute("ageGroups", AgeGroup.values());
			model.addAttribute("languages", Language.values());
			return "book/edit-book";
		}
	}

	@PostMapping("/{name}/delete")
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	public String deleteBook(@PathVariable("name") String name, Model model) {
		try {
			bookService.deleteBookByName(name);
			businessLoggingService.logBookDeleted(name, "anonymous");
			return "redirect:/books";
		} catch (Exception ex) {
			errorLoggingService.logApplicationError("DELETE_BOOK", ex, "anonymous");
			model.addAttribute("errorMessage", "Failed to delete book: " + ex.getMessage());
			return "error/error";
		}
	}
}