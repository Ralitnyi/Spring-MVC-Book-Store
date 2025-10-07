package com.epam.rd.autocode.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;

@Controller
@RequestMapping("/books")
public class BookController {

	private final BookServiceImpl bookService;

	public BookController(BookServiceImpl bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public String listBooks(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
		return "book/books";
	}

	@GetMapping("/add")
	public String showAddBookForm(Model model) {
		model.addAttribute("book", new BookDTO());
		model.addAttribute("ageGroups", AgeGroup.values());
		model.addAttribute("languages", Language.values());
		return "book/add-book";
	}

	@PostMapping("/add")
	public String addBook(@ModelAttribute("book") @jakarta.validation.Valid BookDTO bookDTO,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("ageGroups", AgeGroup.values());
			model.addAttribute("languages", Language.values());
			return "book/add-book";
		}
		bookService.addBook(bookDTO);
		return "redirect:/books";
	}

	@GetMapping("/{name}")
	public String viewBook(@PathVariable("name") String name, Model model) {
		model.addAttribute("book", bookService.getBookByName(name));
		return "book/book-detail";
	}

	@GetMapping("/{name}/edit")
	public String editBookForm(@PathVariable("name") String name, Model model) {
		model.addAttribute("book", bookService.getBookByName(name));
		model.addAttribute("originalName", name);
		model.addAttribute("ageGroups", AgeGroup.values());
		model.addAttribute("languages", Language.values());
		return "book/edit-book";
	}

	@PostMapping("/{name}/edit")
	public String updateBook(@PathVariable("name") String name, @ModelAttribute("book") @jakarta.validation.Valid BookDTO bookDTO,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("originalName", name);
			model.addAttribute("ageGroups", AgeGroup.values());
			model.addAttribute("languages", Language.values());
			return "book/edit-book";
		}
		bookService.updateBookByName(name, bookDTO);
		return "redirect:/books/" + bookDTO.getName();
	}

	@PostMapping("/{name}/delete")
	public String deleteBook(@PathVariable("name") String name) {
		bookService.deleteBookByName(name);
		return "redirect:/books";
	}

}
