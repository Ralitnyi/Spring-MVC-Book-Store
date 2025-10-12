package com.epam.rd.autocode.spring.project.service;

import java.util.List;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;

public interface OrderService {

    List<OrderDTO> getOrdersByClient(String clientEmail);

    List<OrderDTO> getOrdersByEmployee(String employeeEmail);

    OrderDTO addOrder(OrderDTO order);

    List<OrderDTO> getAllOrders();
    
    void confirmOrder(Long id);
}