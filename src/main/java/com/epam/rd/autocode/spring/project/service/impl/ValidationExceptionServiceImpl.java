package com.epam.rd.autocode.spring.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.epam.rd.autocode.spring.project.service.ValidationExceptionService;

@Service
public class ValidationExceptionServiceImpl implements ValidationExceptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionServiceImpl.class);
    
    @Override
    public void handleValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
                logger.warn("Validation error for field {}: {}", error.getField(), error.getDefaultMessage());
            }
            logger.warn("Multiple validation errors: {}", errorMessage.toString());
        }
    }
    
    @Override
    public void handleFieldValidationError(String field, String message) {
        logger.warn("Field validation error for {}: {}", field, message);
    }
    
    @Override
    public void handleCustomValidationError(String message) {
        logger.warn("Custom validation error: {}", message);
    }
    
    @Override
    public void logValidationEvent(String event, String details) {
        logger.info("Validation event: {} - {}", event, details);
    }
}
