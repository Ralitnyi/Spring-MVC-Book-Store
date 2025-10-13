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

import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private BusinessLoggingService businessLoggingService;

    @Mock
    private ErrorLoggingService errorLoggingService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @Test
    void testIndex_Success() {
        String result = homeController.index(model);

        assertEquals("index", result);
        verify(businessLoggingService).logBusinessEvent(eq("HOME_PAGE_REQUESTED"), eq("User requested home page"), eq("anonymous"));
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testIndex_Exception() {
        doThrow(new RuntimeException("Test exception"))
            .when(businessLoggingService)
            .logBusinessEvent(anyString(), anyString(), anyString());

        String result = homeController.index(model);

        assertEquals("error/error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Failed to load home page"));
        verify(errorLoggingService).logApplicationError(eq("HOME_PAGE"), any(Exception.class), eq("anonymous"));
    }
}
