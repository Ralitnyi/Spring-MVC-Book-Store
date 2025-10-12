package com.epam.rd.autocode.spring.project.service;

import org.springframework.validation.BindingResult;

public interface ValidationService {
    void validateBookData(Object bookData, BindingResult bindingResult);
    void validateEmail(String email);
    void validatePassword(String password);
    void validateRequiredField(String field, String fieldName);
    void validatePositiveNumber(Number number, String fieldName);
    void validateStringLength(String value, int maxLength, String fieldName);
}
