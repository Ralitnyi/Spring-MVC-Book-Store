package com.epam.rd.autocode.spring.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.epam.rd.autocode.spring.project.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            EmployeeRepository employeeRepository,
                            BookRepository bookRepository,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail);
        if (client == null) {
            throw new NotFoundException("Client not found: " + clientEmail);
        }
        return orderRepository.findAllByClient(client)
                .stream()
                .map(o -> modelMapper.map(o, OrderDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        Employee employee = employeeRepository.findByEmail(employeeEmail);
        if (employee == null) {
            throw new NotFoundException("Employee not found: " + employeeEmail);
        }
        return orderRepository.findAllByEmployee(employee)
                .stream()
                .map(o -> modelMapper.map(o, OrderDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO orderDTO) {
        if (orderDTO.getClientEmail() == null) {
            throw new NotFoundException("Client email is required");
        }

        Client client = clientRepository.findByEmail(orderDTO.getClientEmail());
        if (client == null) {
            throw new NotFoundException("Client not found: " + orderDTO.getClientEmail());
        }

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(orderDTO.getOrderDate() != null ? orderDTO.getOrderDate() : LocalDateTime.now());

        List<BookItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        if (orderDTO.getBookItems() != null) {
            for (BookItemDTO itemDTO : orderDTO.getBookItems()) {
                Book book = bookRepository.findByName(itemDTO.getBookName());
                if (book == null) {
                    throw new NotFoundException("Book not found: " + itemDTO.getBookName());
                }
                BookItem item = new BookItem();
                item.setOrder(order);
                item.setBook(book);
                item.setQuantity(itemDTO.getQuantity());
                items.add(item);
                if (book.getPrice() != null && itemDTO.getQuantity() != null) {
                    total = total.add(book.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
                }
            }
        }
        order.setBookItems(items);
        order.setPrice(orderDTO.getPrice() != null ? orderDTO.getPrice() : total);

        Order saved = orderRepository.save(order);
        return modelMapper.map(saved, OrderDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public void confirmOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
        order.setConfirmed(true);
        orderRepository.save(order);
    }
}