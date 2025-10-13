package com.epam.rd.autocode.spring.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/test"));
    }

    @Test
    void testHandleError_404() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.NOT_FOUND.value());

        String result = customErrorController.handleError(request, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorCode", "404");
        verify(model).addAttribute("errorMessage", "Page not found");
    }

    @Test
    void testHandleError_500() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        String result = customErrorController.handleError(request, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "Internal server error");
    }

    @Test
    void testHandleError_403() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.FORBIDDEN.value());

        String result = customErrorController.handleError(request, model);

        assertEquals("error/access-denied", result);
        verify(model).addAttribute("errorCode", "403");
        verify(model).addAttribute("errorMessage", "Access denied");
    }

    @Test
    void testHandleError_NoStatusCode() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String result = customErrorController.handleError(request, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "An unexpected error occurred");
    }

    @Test
    void testHandleError_UnknownStatusCode() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(418); // I'm a teapot

        String result = customErrorController.handleError(request, model);

        assertEquals("error/error", result);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "An unexpected error occurred");
    }
}
