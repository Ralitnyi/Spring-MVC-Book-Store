package com.epam.rd.autocode.spring.project.service.impl;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;

@Service
public class ErrorLoggingServiceImpl implements ErrorLoggingService {
    
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger databaseLogger = LoggerFactory.getLogger("DATABASE");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    private static final Logger criticalLogger = LoggerFactory.getLogger("CRITICAL");
    
    @Override
    public void logApplicationError(String operation, Exception exception, String user) {
        errorLogger.error("APPLICATION_ERROR | Operation: {} | User: {} | Error: {} | Timestamp: {}", 
            operation, user, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logSystemError(String operation, Exception exception) {
        errorLogger.error("SYSTEM_ERROR | Operation: {} | Error: {} | Timestamp: {}", 
            operation, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logCriticalError(String operation, Exception exception) {
        criticalLogger.error("CRITICAL_ERROR | Operation: {} | Error: {} | Timestamp: {}", 
            operation, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logDatabaseError(String operation, DataAccessException exception, String user) {
        databaseLogger.error("DATABASE_ERROR | Operation: {} | User: {} | Error: {} | Timestamp: {}", 
            operation, user, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logSQLException(String operation, SQLException exception, String user) {
        databaseLogger.error("SQL_ERROR | Operation: {} | User: {} | SQLState: {} | ErrorCode: {} | Error: {} | Timestamp: {}", 
            operation, user, exception.getSQLState(), exception.getErrorCode(), 
            exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logConnectionError(String operation, Exception exception) {
        databaseLogger.error("CONNECTION_ERROR | Operation: {} | Error: {} | Timestamp: {}", 
            operation, exception.getMessage(), System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logValidationError(String field, String value, String message, String user) {
        errorLogger.warn("VALIDATION_ERROR | Field: {} | Value: {} | Message: {} | User: {} | Timestamp: {}", 
            field, value, message, user, System.currentTimeMillis());
    }
    
    @Override
    public void logConstraintViolation(String constraint, String operation, String user) {
        errorLogger.warn("CONSTRAINT_VIOLATION | Constraint: {} | Operation: {} | User: {} | Timestamp: {}", 
            constraint, operation, user, System.currentTimeMillis());
    }
    
    @Override
    public void logSecurityViolation(String operation, String user, String details) {
        securityLogger.warn("SECURITY_VIOLATION | Operation: {} | User: {} | Details: {} | Timestamp: {}", 
            operation, user, details, System.currentTimeMillis());
    }
    
    @Override
    public void logUnauthorizedAccess(String resource, String user, String ipAddress) {
        securityLogger.warn("UNAUTHORIZED_ACCESS | Resource: {} | User: {} | IP: {} | Timestamp: {}", 
            resource, user, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAuthenticationFailure(String email, String reason, String ipAddress) {
        securityLogger.warn("AUTHENTICATION_FAILURE | Email: {} | Reason: {} | IP: {} | Timestamp: {}", 
            email, reason, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logBusinessRuleViolation(String rule, String operation, String user) {
        errorLogger.warn("BUSINESS_RULE_VIOLATION | Rule: {} | Operation: {} | User: {} | Timestamp: {}", 
            rule, operation, user, System.currentTimeMillis());
    }
    
    @Override
    public void logResourceNotFound(String resourceType, String identifier, String user) {
        errorLogger.warn("RESOURCE_NOT_FOUND | Type: {} | Identifier: {} | User: {} | Timestamp: {}", 
            resourceType, identifier, user, System.currentTimeMillis());
    }
    
    @Override
    public void logDuplicateResource(String resourceType, String identifier, String user) {
        errorLogger.warn("DUPLICATE_RESOURCE | Type: {} | Identifier: {} | User: {} | Timestamp: {}", 
            resourceType, identifier, user, System.currentTimeMillis());
    }
    
    @Override
    public void logSlowOperation(String operation, long durationMs, String user) {
        performanceLogger.warn("SLOW_OPERATION | Operation: {} | Duration: {}ms | User: {} | Timestamp: {}", 
            operation, durationMs, user, System.currentTimeMillis());
    }
    
    @Override
    public void logTimeoutError(String operation, long timeoutMs, String user) {
        performanceLogger.error("TIMEOUT_ERROR | Operation: {} | Timeout: {}ms | User: {} | Timestamp: {}", 
            operation, timeoutMs, user, System.currentTimeMillis());
    }
    
    @Override
    public void logError(String errorType, String operation, String message, String user, Exception exception) {
        errorLogger.error("{} | Operation: {} | User: {} | Message: {} | Timestamp: {}", 
            errorType, operation, user, message, System.currentTimeMillis(), exception);
    }
    
    @Override
    public void logErrorWithContext(String errorType, String operation, String message, String user, 
                                  String context, Exception exception) {
        errorLogger.error("{} | Operation: {} | User: {} | Message: {} | Context: {} | Timestamp: {}", 
            errorType, operation, user, message, context, System.currentTimeMillis(), exception);
    }
}
