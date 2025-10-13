package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;
import com.epam.rd.autocode.spring.project.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private BookServiceImpl bookService;

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @Mock
    private ClientService clientService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private SessionStatus sessionStatus;

    @InjectMocks
    private OrderController orderController;

    private Map<String, Integer> cart;
    private BookDTO book;
    private BookDTO book1;
    private BookDTO book2;

    @BeforeEach
    void setUp() {
        cart = new HashMap<>();

        book = new BookDTO();
        book.setName("Test Book");
        book.setPrice(BigDecimal.valueOf(10.0));

        book1 = new BookDTO();
        book1.setName("Book1");
        book1.setPrice(BigDecimal.valueOf(20.0));

        book2 = new BookDTO();
        book2.setName("Book2");
        book2.setPrice(BigDecimal.valueOf(15.0));
    }

    @Test
    void testInitCart() {
        Map<String, Integer> cart = orderController.initCart();

        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testAddToCart_Success() {
        when(bookService.getBookByName("Test Book")).thenReturn(book);

        String result = orderController.addToCart("Test Book", 2, cart, model);

        assertEquals("redirect:/basket", result);
        assertEquals(2, cart.get("Test Book"));
        verify(businessLoggingService).logItemAddedToCart("Test Book", "anonymous", 2);
    }

    @Test
    void testAddToCart_EmptyBookName() {
        String result = orderController.addToCart("", 2, cart, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorMessage", "Book name cannot be empty");
        verifyNoInteractions(bookService);
    }

    @Test
    void testAddToCart_InvalidQuantity() {
        when(bookService.getBookByName("Test Book")).thenReturn(book);

        String result = orderController.addToCart("Test Book", 0, cart, model);

        assertEquals("redirect:/basket", result);
        assertEquals(1, cart.get("Test Book"));
        verify(businessLoggingService).logItemAddedToCart("Test Book", "anonymous", 1);
    }

    @Test
    void testUpdateCart_AddQuantity() {
        String result = orderController.updateCart("Test Book", 3, cart);

        assertEquals("redirect:/basket", result);
        assertEquals(3, cart.get("Test Book"));
    }

    @Test
    void testUpdateCart_Remove() {
        cart.put("Test Book", 1);

        String result = orderController.updateCart("Test Book", 0, cart);

        assertEquals("redirect:/basket", result);
        assertFalse(cart.containsKey("Test Book"));
    }

    @Test
    void testRemoveFromCart() {
        cart.put("Test Book", 2);

        String result = orderController.removeFromCart("Test Book", cart);

        assertEquals("redirect:/basket", result);
        assertFalse(cart.containsKey("Test Book"));
    }

    @Test
    void testViewCart() {
        cart.put("Book1", 2);
        cart.put("Book2", 1);

        when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));

        String result = orderController.viewCart(cart, model);

        assertEquals("user/basket", result);
        verify(model).addAttribute(eq("items"), anyList());
        verify(model).addAttribute("total", BigDecimal.valueOf(55.0)); // 2*20 + 1*15
    }

    @Test
    void testSubmitOrder_EmptyCart() {
        String result = orderController.submitOrder(cart, principal, sessionStatus, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorMessage", "Cart is empty. Please add items before placing an order.");
    }

    @Test
    void testSubmitOrder_NoPrincipal() {
        cart.put("Test Book", 2);

        String result = orderController.submitOrder(cart, null, sessionStatus, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorMessage", "You must be logged in to place an order.");
    }

    @Test
    void testSubmitOrder_InsufficientBalance() {
        cart.put("Test Book", 2);
        when(principal.getName()).thenReturn("client@test.com");
        when(bookService.getBookByName("Test Book")).thenReturn(book);

        ClientDTO client = new ClientDTO();
        client.setBalance(BigDecimal.valueOf(5.0));
        when(clientService.getClientByEmail("client@test.com")).thenReturn(client);

        String result = orderController.submitOrder(cart, principal, sessionStatus, model);

        assertEquals("error/insufficient-balance", result);
        verify(model).addAttribute("orderTotal", BigDecimal.valueOf(20.0));
        verify(model).addAttribute("currentBalance", BigDecimal.valueOf(5.0));
        verify(model).addAttribute("neededAmount", BigDecimal.valueOf(15.0));
    }

    @Test
    void testSubmitOrder_Success() {
        cart.put("Test Book", 2);
        when(principal.getName()).thenReturn("client@test.com");
        when(bookService.getBookByName("Test Book")).thenReturn(book);

        ClientDTO client = new ClientDTO();
        client.setBalance(BigDecimal.valueOf(25.0));
        when(clientService.getClientByEmail("client@test.com")).thenReturn(client);

        when(orderService.addOrder(any(OrderDTO.class))).thenReturn(any());

        String result = orderController.submitOrder(cart, principal, sessionStatus, model);

        assertEquals("redirect:/orders/my", result);
        verify(clientService).deductBalance("client@test.com", BigDecimal.valueOf(20.0));
        verify(sessionStatus).setComplete();
        verify(businessLoggingService).logOrderCreated("client@test.com", "20.0");
    }

    @Test
    void testMyOrders() {
        when(principal.getName()).thenReturn("client@test.com");
        when(orderService.getOrdersByClient("client@test.com")).thenReturn(new ArrayList<>());

        String result = orderController.myOrders(principal, model);

        assertEquals("orders", result);
        verify(model).addAttribute("orders", new ArrayList<>());
    }

    @Test
    void testMyOrders_NoPrincipal() {
        String result = orderController.myOrders(null, model);

        assertEquals("redirect:/login", result);
        verifyNoInteractions(orderService);
    }
}
