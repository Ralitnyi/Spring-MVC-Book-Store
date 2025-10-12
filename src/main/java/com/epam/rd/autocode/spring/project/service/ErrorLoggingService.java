package com.epam.rd.autocode.spring.project.service;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

public interface ErrorLoggingService {
    
    // Application errors
    void logApplicationError(String operation, Exception exception, String user);
    void logSystemError(String operation, Exception exception);
    void logCriticalError(String operation, Exception exception);
    
    // Database errors
    void logDatabaseError(String operation, DataAccessException exception, String user);
    void logSQLException(String operation, SQLException exception, String user);
    void logConnectionError(String operation, Exception exception);
    
    // Validation errors
    void logValidationError(String field, String value, String message, String user);
    void logConstraintViolation(String constraint, String operation, String user);
    
    // Security errors
    void logSecurityViolation(String operation, String user, String details);
    void logUnauthorizedAccess(String resource, String user, String ipAddress);
    void logAuthenticationFailure(String email, String reason, String ipAddress);
    
    // Business logic errors
    void logBusinessRuleViolation(String rule, String operation, String user);
    void logResourceNotFound(String resourceType, String identifier, String user);
    void logDuplicateResource(String resourceType, String identifier, String user);
    
    // Performance errors
    void logSlowOperation(String operation, long durationMs, String user);
    void logTimeoutError(String operation, long timeoutMs, String user);
    
    // General error logging
    void logError(String errorType, String operation, String message, String user, Exception exception);
    void logErrorWithContext(String errorType, String operation, String message, String user, 
                           String context, Exception exception);
}
