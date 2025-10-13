package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientServiceImpl clientService;

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ClientController clientController;

    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        clientDTO = new ClientDTO();
        clientDTO.setEmail("test@test.com");
        clientDTO.setName("Test Client");
        clientDTO.setBalance(BigDecimal.valueOf(100));
    }

    @Test
    void testGetLoginPage_NoErrors() {
        String result = clientController.getLoginPage(model, null, null);

        assertEquals("user/login", result);
        verify(model).addAttribute(eq("client"), any(ClientDTO.class));
        verify(model, never()).addAttribute(eq("loginError"), anyString());
    }

    @Test
    void testGetLoginPage_WithError() {
        String result = clientController.getLoginPage(model, true, null);

        assertEquals("user/login", result);
        verify(model).addAttribute("loginError", "Invalid email or password");
    }

    @Test
    void testGetLoginPage_WithBlocked() {
        String result = clientController.getLoginPage(model, null, true);

        assertEquals("user/login", result);
        verify(model).addAttribute("loginError", "Your account has been blocked. Please contact support.");
    }

    @Test
    void testGetRegisterPage() {
        String result = clientController.getRegisterPage(model);

        assertEquals("user/register", result);
        verify(model).addAttribute(eq("client"), any(ClientDTO.class));
    }

    @Test
    void testRegister_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = clientController.register(clientDTO, bindingResult, model);

        assertEquals("redirect:/client/login", result);
        verify(clientService).addClient(clientDTO);
        verify(businessLoggingService).logUserRegistered(clientDTO.getEmail(), "CLIENT");
        assertEquals(BigDecimal.ZERO, clientDTO.getBalance());
    }

    @Test
    void testRegister_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = clientController.register(clientDTO, bindingResult, model);

        assertEquals("user/register", result);
        verify(clientService, never()).addClient(any());
        verify(errorLoggingService).logValidationError(eq("client"), anyString(), eq("Validation failed"), eq("anonymous"));
    }

    @Test
    void testRegister_Exception() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Test exception")).when(clientService).addClient(any(ClientDTO.class));

        String result = clientController.register(clientDTO, bindingResult, model);

        assertEquals("user/register", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(errorLoggingService).logApplicationError(eq("CLIENT_REGISTRATION"), any(Exception.class), eq("anonymous"));
    }

    @Test
    void testProfile_Success() {
        when(authentication.getName()).thenReturn("test@test.com");
        when(clientService.getClientByEmail("test@test.com")).thenReturn(clientDTO);

        String result = clientController.profile(model, authentication);

        assertEquals("user/profile-client", result);
        verify(model).addAttribute("client", clientDTO);
    }

    @Test
    void testGetEditPage_Success() {
        when(authentication.getName()).thenReturn("test@test.com");
        when(clientService.getClientByEmail("test@test.com")).thenReturn(clientDTO);

        String result = clientController.getEditPage(model, authentication);

        assertEquals("user/edit-client", result);
        verify(model).addAttribute("client", clientDTO);
    }

    @Test
    void testUpdateProfile_Success() {
        when(authentication.getName()).thenReturn("test@test.com");
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = clientController.updateProfile(clientDTO, bindingResult, model, authentication);

        assertEquals("redirect:/client/profile", result);
        verify(clientService).updateClientByEmail("test@test.com", clientDTO);
    }

    @Test
    void testUpdateProfile_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = clientController.updateProfile(clientDTO, bindingResult, model, authentication);

        assertEquals("user/edit-client", result);
        verify(clientService, never()).updateClientByEmail(anyString(), any());
    }

    @Test
    void testDeleteAccount_Success() throws ServletException {
        when(authentication.getName()).thenReturn("test@test.com");

        String result = clientController.deleteAccount(authentication, request);

        assertEquals("redirect:/", result);
        verify(clientService).deleteClientByEmail("test@test.com");
        verify(request).logout();
    }
}
