package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.epam.rd.autocode.spring.project.exception.ValidationException;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl validationService;

    @Mock
    private Object bookData;

    @Mock
    private BindingResult bindingResult;

    @Test
    void testValidateBookDataNullBookData() {
        assertThrows(ValidationException.class, () -> validationService.validateBookData(null, bindingResult));
    }

    @Test
    void testValidateBookDataWithErrors() {
        FieldError error = new FieldError("object", "field", "Error message");

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error));

        assertThrows(ValidationException.class, () -> validationService.validateBookData(bookData, bindingResult));
    }

    @Test
    void testValidateBookDataNoErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);

        validationService.validateBookData(bookData, bindingResult);

        assertTrue(true);
    }

    @Test
    void testValidateEmailNull() {
        assertThrows(ValidationException.class, () -> validationService.validateEmail(null));
    }

    @Test
    void testValidateEmailEmpty() {
        assertThrows(ValidationException.class, () -> validationService.validateEmail(""));
    }

    @Test
    void testValidateEmailInvalidFormat() {
        assertThrows(ValidationException.class, () -> validationService.validateEmail("invalid-email"));
    }

    @Test
    void testValidateEmailTooLong() {
        String longEmail = "a".repeat(250) + "@example.com";
        assertThrows(ValidationException.class, () -> validationService.validateEmail(longEmail));
    }

    @Test
    void testValidateEmailValid() {
        validationService.validateEmail("test@example.com");
        assertTrue(true);
    }

    @Test
    void testValidatePasswordNull() {
        assertThrows(ValidationException.class, () -> validationService.validatePassword(null));
    }

    @Test
    void testValidatePasswordEmpty() {
        assertThrows(ValidationException.class, () -> validationService.validatePassword(""));
    }

    @Test
    void testValidatePasswordTooShort() {
        assertThrows(ValidationException.class, () -> validationService.validatePassword("123"));
    }

    @Test
    void testValidatePasswordTooLong() {
        String longPassword = "a".repeat(101); // More than 100 characters
        assertThrows(ValidationException.class, () -> validationService.validatePassword(longPassword));
    }

    @Test
    void testValidatePasswordValid() {
        validationService.validatePassword("validPassword123");
        assertTrue(true);
    }

    @Test
    void testValidateRequiredFieldNull() {
        assertThrows(ValidationException.class, () -> validationService.validateRequiredField(null, "FieldName"));
    }

    @Test
    void testValidateRequiredFieldEmpty() {
        assertThrows(ValidationException.class, () -> validationService.validateRequiredField("", "FieldName"));
    }

    @Test
    void testValidateRequiredFieldValid() {
        validationService.validateRequiredField("value", "FieldName");
        assertTrue(true);
    }

    @Test
    void testValidatePositiveNumberNull() {
        assertThrows(ValidationException.class, () -> validationService.validatePositiveNumber(null, "FieldName"));
    }

    @Test
    void testValidatePositiveNumberZero() {
        assertThrows(ValidationException.class, () -> validationService.validatePositiveNumber(0, "FieldName"));
    }

    @Test
    void testValidatePositiveNumberNegative() {
        assertThrows(ValidationException.class, () -> validationService.validatePositiveNumber(-1, "FieldName"));
    }

    @Test
    void testValidatePositiveNumberValid() {
        validationService.validatePositiveNumber(1, "FieldName");
        validationService.validatePositiveNumber(1.5, "FieldName");
        assertTrue(true);
    }

    @Test
    void testValidateStringLengthNull() {
        validationService.validateStringLength(null, 10, "FieldName");
        assertTrue(true);
    }

    @Test
    void testValidateStringLengthExceedsMax() {
        assertThrows(ValidationException.class, () -> validationService.validateStringLength("a".repeat(11), 10, "FieldName"));
    }

    @Test
    void testValidateStringLengthValid() {
        validationService.validateStringLength("short", 10, "FieldName");
        validationService.validateStringLength("", 10, "FieldName");
        assertTrue(true);
    }

    @Test
    void testValidateStringLengthExactMax() {
        validationService.validateStringLength("a".repeat(10), 10, "FieldName");
        assertTrue(true);
    }
}
