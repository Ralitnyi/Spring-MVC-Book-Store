package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            EmployeeRepository employeeRepository,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
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
        Order order = modelMapper.map(orderDTO, Order.class);
        Order saved = orderRepository.save(order);
        return modelMapper.map(saved, OrderDTO.class);
    }
}
