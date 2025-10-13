package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("employee@test.com");
        employeeDTO.setName("Test Employee");
    }

    @Test
    void testProfile_Success() {
        when(authentication.getName()).thenReturn("employee@test.com");
        when(employeeService.getEmployeeByEmail("employee@test.com")).thenReturn(employeeDTO);

        String result = employeeController.profile(model, authentication);

        assertEquals("user/profile-employee", result);
        verify(model).addAttribute("employee", employeeDTO);
        verify(employeeService).getEmployeeByEmail("employee@test.com");
    }

    @Test
    void testGetEditPage_Success() {
        when(authentication.getName()).thenReturn("employee@test.com");
        when(employeeService.getEmployeeByEmail("employee@test.com")).thenReturn(employeeDTO);

        String result = employeeController.getEditPage(model, authentication);

        assertEquals("user/edit-employee", result);
        verify(model).addAttribute("employee", employeeDTO);
        verify(employeeService).getEmployeeByEmail("employee@test.com");
    }

    @Test
    void testUpdateProfile_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn("employee@test.com");

        String result = employeeController.updateProfile(employeeDTO, bindingResult, model, authentication);

        assertEquals("redirect:/employee/profile", result);
        verify(employeeService).updateEmployeeByEmail("employee@test.com", employeeDTO);
    }

    @Test
    void testUpdateProfile_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = employeeController.updateProfile(employeeDTO, bindingResult, model, authentication);

        assertEquals("user/edit-employee", result);
        verify(employeeService, never()).updateEmployeeByEmail(anyString(), any(EmployeeDTO.class));
    }

    @Test
    void testDeleteAccount_Success() throws ServletException {
        when(authentication.getName()).thenReturn("employee@test.com");

        String result = employeeController.deleteAccount(authentication, request);

        assertEquals("redirect:/", result);
        verify(employeeService).deleteEmployeeByEmail("employee@test.com");
        verify(request).logout();
    }
}
