package com.epam.rd.autocode.spring.project.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;
import com.epam.rd.autocode.spring.project.service.impl.OrderServiceImpl;

@Controller
@RequestMapping
@SessionAttributes("cart")
public class OrderController {

    private final BookServiceImpl bookService;
    private final OrderServiceImpl orderService;
    private final BusinessLoggingService businessLoggingService;
    private final ErrorLoggingService errorLoggingService;
    private final ClientService clientService;

    public OrderController(BookServiceImpl bookService, OrderServiceImpl orderService,
                          BusinessLoggingService businessLoggingService,
                          ErrorLoggingService errorLoggingService,
                          ClientService clientService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.businessLoggingService = businessLoggingService;
        this.errorLoggingService = errorLoggingService;
        this.clientService = clientService;
    }

    @ModelAttribute("cart")
    public Map<String, Integer> initCart() {
        return new HashMap<>();
    }

    @PostMapping("/cart/add")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String addToCart(@RequestParam("name") String bookName,
                            @RequestParam(name = "quantity", required = false, defaultValue = "1") Integer quantity,
                            @ModelAttribute("cart") Map<String, Integer> cart,
                            Model model) {
        try {
            if (bookName == null || bookName.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Book name cannot be empty");
                return "error/error";
            }
            if (quantity == null || quantity < 1) {
                quantity = 1;
            }
            
            bookService.getBookByName(bookName);
            
            cart.merge(bookName, quantity, Integer::sum);
            businessLoggingService.logItemAddedToCart(bookName, "anonymous", quantity);
            return "redirect:/basket";
        } catch (Exception ex) {
            errorLoggingService.logApplicationError("ADD_TO_CART", ex, "anonymous");
            model.addAttribute("errorMessage", "Failed to add book to cart: " + ex.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/cart/update")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String updateCart(@RequestParam("name") String bookName,
                             @RequestParam("quantity") Integer quantity,
                             @ModelAttribute("cart") Map<String, Integer> cart) {
        if (quantity == null || quantity < 1) {
            cart.remove(bookName);
        } else {
            cart.put(bookName, quantity);
        }
        return "redirect:/basket";
    }

    @PostMapping("/cart/remove")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String removeFromCart(@RequestParam("name") String bookName,
                                 @ModelAttribute("cart") Map<String, Integer> cart) {
        cart.remove(bookName);
        return "redirect:/basket";
    }

    @GetMapping("/basket")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String viewCart(@ModelAttribute("cart") Map<String, Integer> cart, Model model) {
        List<BookDTO> books = bookService.getAllBooks();
        List<BookItemDTO> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String name = entry.getKey();
            Integer qty = entry.getValue();
            BookDTO book = books.stream().filter(b -> b.getName().equals(name)).findFirst().orElse(null);
            if (book != null) {
                items.add(new BookItemDTO(name, qty));
                if (book.getPrice() != null) {
                    total = total.add(book.getPrice().multiply(BigDecimal.valueOf(qty)));
                }
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "user/basket";
    }

    @PostMapping("/orders/submit")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String submitOrder(@ModelAttribute("cart") Map<String, Integer> cart,
                              Principal principal,
                              SessionStatus sessionStatus,
                              Model model) {
        try {
            if (cart.isEmpty()) {
                model.addAttribute("errorMessage", "Cart is empty. Please add items before placing an order.");
                return "error/error";
            }
            
            if (principal == null) {
                model.addAttribute("errorMessage", "You must be logged in to place an order.");
                return "error/error";
            }
            
            BigDecimal orderTotal = BigDecimal.ZERO;
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                String name = entry.getKey();
                Integer qty = entry.getValue();
                BookDTO book = bookService.getBookByName(name);
                if (book != null && book.getPrice() != null) {
                    orderTotal = orderTotal.add(book.getPrice().multiply(BigDecimal.valueOf(qty)));
                }
            }
            
            BigDecimal currentBalance = BigDecimal.ZERO;
            try {
                currentBalance = clientService.getClientByEmail(principal.getName()).getBalance();
                if (currentBalance == null) {
                    currentBalance = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                currentBalance = BigDecimal.ZERO;
            }
            
            if (currentBalance.compareTo(orderTotal) < 0) {
                BigDecimal neededAmount = orderTotal.subtract(currentBalance);
                model.addAttribute("orderTotal", orderTotal);
                model.addAttribute("currentBalance", currentBalance);
                model.addAttribute("neededAmount", neededAmount);
                
                errorLoggingService.logApplicationError("INSUFFICIENT_BALANCE", 
                    new RuntimeException("Insufficient balance: " + currentBalance + " < " + orderTotal), 
                    principal.getName());
                
                return "error/insufficient-balance";
            }
            
            OrderDTO order = new OrderDTO();
            order.setClientEmail(principal.getName());
            order.setOrderDate(LocalDateTime.now());

            List<BookItemDTO> bookItems = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                String name = entry.getKey();
                Integer qty = entry.getValue();
                BookDTO book = bookService.getBookByName(name);
                if (book != null) {
                    bookItems.add(new BookItemDTO(name, qty));
                }
            }
            order.setBookItems(bookItems);
            order.setPrice(orderTotal);

            orderService.addOrder(order);
            
            try {
                clientService.deductBalance(principal.getName(), orderTotal);
            } catch (Exception e) {
                errorLoggingService.logApplicationError("BALANCE_DEDUCTION_FAILED", e, principal.getName());
            }
            
            businessLoggingService.logOrderCreated(order.getClientEmail(), order.getPrice().toString());
            sessionStatus.setComplete();
            model.addAttribute("order", order);
            return "redirect:/orders/my";
        } catch (Exception ex) {
            errorLoggingService.logApplicationError("SUBMIT_ORDER", ex, principal != null ? principal.getName() : "anonymous");
            model.addAttribute("errorMessage", "Failed to submit order: " + ex.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/orders/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String myOrders(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrdersByClient(principal.getName()));
        return "orders";
    }
}