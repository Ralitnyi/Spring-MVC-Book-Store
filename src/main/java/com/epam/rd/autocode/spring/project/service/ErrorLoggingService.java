package com.epam.rd.autocode.spring.project.service;

import org.springframework.dao.DataAccessException;

public interface ErrorLoggingService {
    
    void logApplicationError(String operation, Exception exception, String user);
    
    void logDatabaseError(String operation, DataAccessException exception, String user);
    
    void logValidationError(String field, String value, String message, String user);
}
