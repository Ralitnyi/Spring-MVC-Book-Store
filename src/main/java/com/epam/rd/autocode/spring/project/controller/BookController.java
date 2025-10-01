package com.epam.rd.autocode.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
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
        return "books";
    }
    
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
    	model.addAttribute("book", new BookDTO());
    	return "add-book";
    }
    
    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") BookDTO bookDTO) {
        bookService.addBook(bookDTO);
        return "redirect:/books";
    }

}
