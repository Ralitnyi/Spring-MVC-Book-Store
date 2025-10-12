package com.epam.rd.autocode.spring.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final EmployeeServiceImpl employeeServiceImpl;
    private final ClientService clientService;
    private final OrderService orderService;
    private final BusinessLoggingService businessLoggingService;
    private final ErrorLoggingService errorLoggingService;

    public AdminController(EmployeeServiceImpl employeeServiceImpl, ClientService clientService, OrderService orderService,
                          BusinessLoggingService businessLoggingService, ErrorLoggingService errorLoggingService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.clientService = clientService;
        this.orderService = orderService;
        this.businessLoggingService = businessLoggingService;
        this.errorLoggingService = errorLoggingService;
    }

    @GetMapping("/create-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public String getCreateEmployeePage(Model model) {
        try {
            logger.debug("Admin requested create employee page");
            businessLoggingService.logBusinessEvent("CREATE_EMPLOYEE_PAGE_REQUESTED", "Admin requested create employee page", "admin");
            model.addAttribute("employee", new EmployeeDTO());
            return "admin/create-employee";
        } catch (Exception ex) {
            logger.error("Error loading create employee page", ex);
            errorLoggingService.logApplicationError("CREATE_EMPLOYEE_PAGE", ex, "admin");
            model.addAttribute("errorMessage", "Failed to load create employee page: " + ex.getMessage());
            return "error/error";
        }
    }
    
    @PostMapping("/employee")
    @PreAuthorize("hasRole('ADMIN')")
    public String createEmployee(@ModelAttribute("employee") @Valid EmployeeDTO employeeDTO,
			BindingResult bindingResult,
			Model model) {
        try {
            logger.debug("Admin attempting to create employee: {}", employeeDTO.getEmail());
            
            if (bindingResult.hasErrors()) {
                logger.warn("Validation errors in employee creation: {}", bindingResult.getAllErrors());
                errorLoggingService.logValidationError("employee", employeeDTO.toString(), "Validation failed", "admin");
                return "admin/create-employee";
            }
            
            employeeServiceImpl.addEmployee(employeeDTO);
            logger.info("Successfully created employee: {}", employeeDTO.getEmail());
            businessLoggingService.logBusinessEvent("EMPLOYEE_CREATED", "Employee created: " + employeeDTO.getEmail(), "admin");
            return "redirect:/admin/clients";
        } catch (Exception ex) {
            logger.error("Error creating employee: {}", employeeDTO.getEmail(), ex);
            errorLoggingService.logApplicationError("CREATE_EMPLOYEE", ex, "admin");
            model.addAttribute("errorMessage", "Failed to create employee: " + ex.getMessage());
            return "admin/create-employee";
        }
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String getAllClients(Model model) {
        try {
            logger.debug("Admin/Employee requested clients list");
            businessLoggingService.logBusinessEvent("CLIENTS_LIST_REQUESTED", "Admin/Employee requested clients list", "admin");
            model.addAttribute("clients", clientService.getAllClients());
            return "admin/clients";
        } catch (Exception ex) {
            logger.error("Error loading clients list", ex);
            errorLoggingService.logApplicationError("GET_CLIENTS_LIST", ex, "admin");
            model.addAttribute("errorMessage", "Failed to load clients: " + ex.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/clients/block")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String blockClient(@RequestParam("email") String email, Model model) {
        try {
            logger.info("Admin/Employee attempting to block client: {}", email);
            businessLoggingService.logBusinessEvent("CLIENT_BLOCK_REQUESTED", "Attempting to block client: " + email, "admin");
            
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Attempt to block client with empty email");
                model.addAttribute("errorMessage", "Client email cannot be empty");
                return "error/error";
            }
            
            clientService.blockClient(email);
            logger.info("Successfully blocked client: {}", email);
            businessLoggingService.logBusinessEvent("CLIENT_BLOCKED", "Client blocked: " + email, "admin");
            return "redirect:/admin/clients";
        } catch (Exception ex) {
            logger.error("Error blocking client: {}", email, ex);
            errorLoggingService.logApplicationError("BLOCK_CLIENT", ex, "admin");
            model.addAttribute("errorMessage", "Failed to block client: " + ex.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/clients/unblock")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String unblockClient(@RequestParam("email") String email, Model model) {
        try {
            logger.info("Admin/Employee attempting to unblock client: {}", email);
            businessLoggingService.logBusinessEvent("CLIENT_UNBLOCK_REQUESTED", "Attempting to unblock client: " + email, "admin");
            
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Attempt to unblock client with empty email");
                model.addAttribute("errorMessage", "Client email cannot be empty");
                return "error/error";
            }
            
            clientService.unblockClient(email);
            logger.info("Successfully unblocked client: {}", email);
            businessLoggingService.logBusinessEvent("CLIENT_UNBLOCKED", "Client unblocked: " + email, "admin");
            return "redirect:/admin/clients";
        } catch (Exception ex) {
            logger.error("Error unblocking client: {}", email, ex);
            errorLoggingService.logApplicationError("UNBLOCK_CLIENT", ex, "admin");
            model.addAttribute("errorMessage", "Failed to unblock client: " + ex.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String getAllOrders(Model model) {
        try {
            logger.debug("Admin/Employee requested orders list");
            businessLoggingService.logBusinessEvent("ORDERS_LIST_REQUESTED", "Admin/Employee requested orders list", "admin");
            model.addAttribute("orders", orderService.getAllOrders());
            return "admin/orders";
        } catch (Exception ex) {
            logger.error("Error loading orders list", ex);
            errorLoggingService.logApplicationError("GET_ORDERS_LIST", ex, "admin");
            model.addAttribute("errorMessage", "Failed to load orders: " + ex.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/orders/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String confirmOrder(@RequestParam("orderId") Long orderId, Model model) {
        try {
            logger.info("Admin/Employee attempting to confirm order: {}", orderId);
            businessLoggingService.logBusinessEvent("ORDER_CONFIRM_REQUESTED", "Attempting to confirm order: " + orderId, "admin");
            
            if (orderId == null || orderId <= 0) {
                logger.warn("Attempt to confirm order with invalid ID: {}", orderId);
                model.addAttribute("errorMessage", "Invalid order ID");
                return "error/error";
            }
            
            orderService.confirmOrder(orderId);
            logger.info("Successfully confirmed order: {}", orderId);
            businessLoggingService.logBusinessEvent("ORDER_CONFIRMED", "Order confirmed: " + orderId, "admin");
            return "redirect:/admin/orders";
        } catch (Exception ex) {
            logger.error("Error confirming order: {}", orderId, ex);
            errorLoggingService.logApplicationError("CONFIRM_ORDER", ex, "admin");
            model.addAttribute("errorMessage", "Failed to confirm order: " + ex.getMessage());
            return "error/error";
        }
    }
}
