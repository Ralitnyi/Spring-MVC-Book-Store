package com.epam.rd.autocode.spring.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;

@Service
public class ErrorLoggingServiceImpl implements ErrorLoggingService {
    
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
    private static final Logger databaseLogger = LoggerFactory.getLogger("DATABASE");
    @Override
    public void logApplicationError(String operation, Exception exception, String user) {
        errorLogger.error("APPLICATION_ERROR | Operation: {} | User: {} | Error: {} | Timestamp: {}", 
            operation, user, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logDatabaseError(String operation, DataAccessException exception, String user) {
        databaseLogger.error("DATABASE_ERROR | Operation: {} | User: {} | Error: {} | Timestamp: {}", 
            operation, user, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logValidationError(String field, String value, String message, String user) {
        errorLogger.warn("VALIDATION_ERROR | Field: {} | Value: {} | Message: {} | User: {} | Timestamp: {}", 
            field, value, message, user, System.currentTimeMillis());
    }
}
