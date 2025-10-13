package com.epam.rd.autocode.spring.project.service;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

public interface ErrorLoggingService {
    
    void logApplicationError(String operation, Exception exception, String user);
    void logSystemError(String operation, Exception exception);
    void logCriticalError(String operation, Exception exception);
    
    void logDatabaseError(String operation, DataAccessException exception, String user);
    void logSQLException(String operation, SQLException exception, String user);
    void logConnectionError(String operation, Exception exception);
    
    void logValidationError(String field, String value, String message, String user);
    void logConstraintViolation(String constraint, String operation, String user);
    
    void logSecurityViolation(String operation, String user, String details);
    void logUnauthorizedAccess(String resource, String user, String ipAddress);
    void logAuthenticationFailure(String email, String reason, String ipAddress);
    
    void logBusinessRuleViolation(String rule, String operation, String user);
    void logResourceNotFound(String resourceType, String identifier, String user);
    void logDuplicateResource(String resourceType, String identifier, String user);
    
    void logSlowOperation(String operation, long durationMs, String user);
    void logTimeoutError(String operation, long timeoutMs, String user);
    
    void logError(String errorType, String operation, String message, String user, Exception exception);
    void logErrorWithContext(String errorType, String operation, String message, String user, 
                           String context, Exception exception);
}
