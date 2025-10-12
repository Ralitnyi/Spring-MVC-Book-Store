package com.epam.rd.autocode.spring.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.epam.rd.autocode.spring.project.exception.ValidationException;
import com.epam.rd.autocode.spring.project.service.ValidationService;

import java.util.regex.Pattern;

@Service
public class ValidationServiceImpl implements ValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    @Override
    public void validateBookData(Object bookData, BindingResult bindingResult) {
        if (bookData == null) {
            logger.warn("Book data is null");
            throw new ValidationException("Book data cannot be null");
        }
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            logger.warn("Book validation failed: {}", errorMessage.toString());
            throw new ValidationException(errorMessage.toString());
        }
    }
    
    @Override
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Email is null or empty");
            throw new ValidationException("Email cannot be null or empty");
        }
        
        if (!emailPattern.matcher(email).matches()) {
            logger.warn("Invalid email format: {}", email);
            throw new ValidationException("Invalid email format");
        }
        
        if (email.length() > 255) {
            logger.warn("Email too long: {}", email);
            throw new ValidationException("Email cannot exceed 255 characters");
        }
    }
    
    @Override
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            logger.warn("Password is null or empty");
            throw new ValidationException("Password cannot be null or empty");
        }
        
        if (password.length() < 6) {
            logger.warn("Password too short");
            throw new ValidationException("Password must be at least 6 characters long");
        }
        
        if (password.length() > 100) {
            logger.warn("Password too long");
            throw new ValidationException("Password cannot exceed 100 characters");
        }
    }
    
    @Override
    public void validateRequiredField(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            logger.warn("Required field {} is null or empty", fieldName);
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }
    
    @Override
    public void validatePositiveNumber(Number number, String fieldName) {
        if (number == null) {
            logger.warn("Number field {} is null", fieldName);
            throw new ValidationException(fieldName + " cannot be null");
        }
        
        if (number.doubleValue() <= 0) {
            logger.warn("Number field {} is not positive: {}", fieldName, number);
            throw new ValidationException(fieldName + " must be a positive number");
        }
    }
    
    @Override
    public void validateStringLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            logger.warn("String field {} exceeds maximum length: {}", fieldName, value.length());
            throw new ValidationException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
}
