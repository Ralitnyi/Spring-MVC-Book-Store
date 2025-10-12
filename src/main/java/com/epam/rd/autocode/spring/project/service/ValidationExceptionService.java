package com.epam.rd.autocode.spring.project.service;

import org.springframework.validation.BindingResult;

public interface ValidationExceptionService {
    void handleValidationErrors(BindingResult bindingResult);
    void handleFieldValidationError(String field, String message);
    void handleCustomValidationError(String message);
    void logValidationEvent(String event, String details);
}
