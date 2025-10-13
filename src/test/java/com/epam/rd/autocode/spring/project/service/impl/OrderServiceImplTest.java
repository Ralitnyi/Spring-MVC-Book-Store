package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client client;
    private Employee employee;
    private Book book;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail("client@test.com");

        employee = new Employee();
        employee.setEmail("employee@test.com");

        book = new Book();
        book.setName("Test Book");
        book.setPrice(BigDecimal.valueOf(10.0));

        order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setEmployee(employee);
        order.setConfirmed(false);

        BookItemDTO bookItemDTO = new BookItemDTO();
        bookItemDTO.setBookName("Test Book");
        bookItemDTO.setQuantity(2);

        orderDTO = new OrderDTO();
        orderDTO.setClientEmail("client@test.com");
        orderDTO.setOrderDate(LocalDateTime.now());
        orderDTO.setBookItems(List.of(bookItemDTO));
    }

    @Test
    void testGetOrdersByClient_Success() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(orderRepository.findAllByClient(client)).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getOrdersByClient("client@test.com");

        assertEquals(1, result.size());
        assertEquals(orderDTO, result.get(0));
    }

    @Test
    void testGetOrdersByClient_ClientNotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.getOrdersByClient("nonexistent@test.com"));
    }

    @Test
    void testGetOrdersByEmployee_Success() {
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(employee);
        when(orderRepository.findAllByEmployee(employee)).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getOrdersByEmployee("employee@test.com");

        assertEquals(1, result.size());
        assertEquals(orderDTO, result.get(0));
    }

    @Test
    void testGetOrdersByEmployee_EmployeeNotFound() {
        when(employeeRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.getOrdersByEmployee("nonexistent@test.com"));
    }

    @Test
    void testAddOrder_Success() {
        Order savedOrder = new Order();
        savedOrder.setId(1L);

        BookItemDTO itemDTO = new BookItemDTO();
        itemDTO.setBookName("Test Book");
        itemDTO.setQuantity(2);

        OrderDTO newOrderDTO = new OrderDTO();
        newOrderDTO.setClientEmail("client@test.com");
        newOrderDTO.setBookItems(List.of(itemDTO));

        BookItem bookItem = new BookItem();
        bookItem.setBook(book);
        bookItem.setQuantity(2);

        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(bookRepository.findByName("Test Book")).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.addOrder(newOrderDTO);

        assertEquals(orderDTO, result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testAddOrder_ClientNotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        OrderDTO invalidOrderDTO = new OrderDTO();
        invalidOrderDTO.setClientEmail("nonexistent@test.com");

        assertThrows(NotFoundException.class, () -> orderService.addOrder(invalidOrderDTO));
    }

    @Test
    void testAddOrder_BookNotFound() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(bookRepository.findByName("Nonexistent Book")).thenReturn(null);

        BookItemDTO itemDTO = new BookItemDTO();
        itemDTO.setBookName("Nonexistent Book");
        itemDTO.setQuantity(1);

        OrderDTO newOrderDTO = new OrderDTO();
        newOrderDTO.setClientEmail("client@test.com");
        newOrderDTO.setBookItems(List.of(itemDTO));

        assertThrows(NotFoundException.class, () -> orderService.addOrder(newOrderDTO));
    }

    @Test
    void testAddOrder_NoClientEmail() {
        OrderDTO invalidOrderDTO = new OrderDTO();

        assertThrows(NotFoundException.class, () -> orderService.addOrder(invalidOrderDTO));
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(orderDTO, result.get(0));
    }

    @Test
    void testConfirmOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.confirmOrder(1L);

        assertTrue(order.isConfirmed());
        verify(orderRepository).save(order);
    }

    @Test
    void testConfirmOrder_OrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.confirmOrder(999L));
    }
}
