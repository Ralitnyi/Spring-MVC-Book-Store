package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private EmployeeServiceImpl employeeServiceImpl;

    @Mock
    private ClientService clientService;

    @Mock
    private OrderService orderService;

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AdminController adminController;

    private EmployeeDTO employeeDTO;
    private ClientDTO clientDTO;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("employee@test.com");
        employeeDTO.setName("Test Employee");
        employeeDTO.setPassword("password123");

        clientDTO = new ClientDTO();
        clientDTO.setEmail("client@test.com");
        clientDTO.setName("Test Client");

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
    }

    @Test
    void testGetCreateEmployeePage_Success() {
        String result = adminController.getCreateEmployeePage(model);

        assertEquals("admin/create-employee", result);
        verify(model).addAttribute(eq("employee"), any(EmployeeDTO.class));
        verify(businessLoggingService).logBusinessEvent(eq("CREATE_EMPLOYEE_PAGE_REQUESTED"), anyString(), eq("admin"));
    }

    @Test
    void testGetCreateEmployeePage_Exception() {
        doThrow(new RuntimeException("Test exception"))
            .when(businessLoggingService)
            .logBusinessEvent(anyString(), anyString(), anyString());

        String result = adminController.getCreateEmployeePage(model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("CREATE_EMPLOYEE_PAGE"), any(Exception.class), eq("admin"));
    }

    @Test
    void testCreateEmployee_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = adminController.createEmployee(employeeDTO, bindingResult, model);

        assertEquals("redirect:/home", result);
        verify(employeeServiceImpl).addEmployee(employeeDTO);
        verify(businessLoggingService).logBusinessEvent(eq("EMPLOYEE_CREATED"), anyString(), eq("admin"));
    }

    @Test
    void testCreateEmployee_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = adminController.createEmployee(employeeDTO, bindingResult, model);

        assertEquals("admin/create-employee", result);
        verify(employeeServiceImpl, never()).addEmployee(any());
        verify(errorLoggingService).logValidationError(eq("employee"), anyString(), eq("Validation failed"), eq("admin"));
    }

    @Test
    void testCreateEmployee_Exception() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Test exception"))
            .when(employeeServiceImpl)
            .addEmployee(any(EmployeeDTO.class));

        String result = adminController.createEmployee(employeeDTO, bindingResult, model);

        assertEquals("admin/create-employee", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("CREATE_EMPLOYEE"), any(Exception.class), eq("admin"));
    }

    @Test
    void testGetAllClients_Success() {
        List<ClientDTO> clients = Arrays.asList(clientDTO);
        when(clientService.getAllClients()).thenReturn(clients);

        String result = adminController.getAllClients(model);

        assertEquals("admin/clients", result);
        verify(model).addAttribute("clients", clients);
        verify(businessLoggingService).logBusinessEvent(eq("CLIENTS_LIST_REQUESTED"), anyString(), eq("admin"));
    }

    @Test
    void testGetAllClients_Exception() {
        when(clientService.getAllClients()).thenThrow(new RuntimeException("Test exception"));

        String result = adminController.getAllClients(model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("GET_CLIENTS_LIST"), any(Exception.class), eq("admin"));
    }

    @Test
    void testBlockClient_Success() {
        String email = "client@test.com";

        String result = adminController.blockClient(email, model);

        assertEquals("redirect:/admin/clients", result);
        verify(clientService).blockClient(email);
        verify(businessLoggingService).logBusinessEvent(eq("CLIENT_BLOCKED"), anyString(), eq("admin"));
    }

    @Test
    void testBlockClient_EmptyEmail() {
        String result = adminController.blockClient("", model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), eq("Client email cannot be empty"));
        verify(clientService, never()).blockClient(anyString());
    }

    @Test
    void testBlockClient_Exception() {
        String email = "client@test.com";
        doThrow(new RuntimeException("Test exception"))
            .when(clientService)
            .blockClient(anyString());

        String result = adminController.blockClient(email, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("BLOCK_CLIENT"), any(Exception.class), eq("admin"));
    }

    @Test
    void testUnblockClient_Success() {
        String email = "client@test.com";

        String result = adminController.unblockClient(email, model);

        assertEquals("redirect:/admin/clients", result);
        verify(clientService).unblockClient(email);
        verify(businessLoggingService).logBusinessEvent(eq("CLIENT_UNBLOCKED"), anyString(), eq("admin"));
    }

    @Test
    void testUnblockClient_EmptyEmail() {
        String result = adminController.unblockClient("", model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), eq("Client email cannot be empty"));
        verify(clientService, never()).unblockClient(anyString());
    }

    @Test
    void testUnblockClient_Exception() {
        String email = "client@test.com";
        doThrow(new RuntimeException("Test exception"))
            .when(clientService)
            .unblockClient(anyString());

        String result = adminController.unblockClient(email, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("UNBLOCK_CLIENT"), any(Exception.class), eq("admin"));
    }

    @Test
    void testGetAllOrders_Success() {
        List<OrderDTO> orders = Arrays.asList(orderDTO);
        when(orderService.getAllOrders()).thenReturn(orders);

        String result = adminController.getAllOrders(model);

        assertEquals("admin/orders", result);
        verify(model).addAttribute("orders", orders);
        verify(businessLoggingService).logBusinessEvent(eq("ORDERS_LIST_REQUESTED"), anyString(), eq("admin"));
    }

    @Test
    void testGetAllOrders_Exception() {
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("Test exception"));

        String result = adminController.getAllOrders(model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("GET_ORDERS_LIST"), any(Exception.class), eq("admin"));
    }

    @Test
    void testConfirmOrder_Success() {
        Long orderId = 1L;

        String result = adminController.confirmOrder(orderId, model);

        assertEquals("redirect:/admin/orders", result);
        verify(orderService).confirmOrder(orderId);
        verify(businessLoggingService).logBusinessEvent(eq("ORDER_CONFIRMED"), anyString(), eq("admin"));
    }

    @Test
    void testConfirmOrder_InvalidId() {
        Long orderId = -1L;

        String result = adminController.confirmOrder(orderId, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), eq("Invalid order ID"));
        verify(orderService, never()).confirmOrder(anyLong());
    }

    @Test
    void testConfirmOrder_NullId() {
        String result = adminController.confirmOrder(null, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), eq("Invalid order ID"));
        verify(orderService, never()).confirmOrder(anyLong());
    }

    @Test
    void testConfirmOrder_Exception() {
        Long orderId = 1L;
        doThrow(new RuntimeException("Test exception"))
            .when(orderService)
            .confirmOrder(anyLong());

        String result = adminController.confirmOrder(orderId, model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("CONFIRM_ORDER"), any(Exception.class), eq("admin"));
    }
}
