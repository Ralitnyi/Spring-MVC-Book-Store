package com.epam.rd.autocode.spring.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;

@Service
public class BusinessLoggingServiceImpl implements BusinessLoggingService {
    
    private static final Logger businessLogger = LoggerFactory.getLogger("BUSINESS");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    private static final Logger dataAccessLogger = LoggerFactory.getLogger("DATA_ACCESS");
    
    // Book operations
    @Override
    public void logBookCreated(String bookName, String user) {
        businessLogger.info("BOOK_CREATED | Book: {} | User: {} | Timestamp: {}", 
            bookName, user, System.currentTimeMillis());
    }
    
    @Override
    public void logBookUpdated(String bookName, String user) {
        businessLogger.info("BOOK_UPDATED | Book: {} | User: {} | Timestamp: {}", 
            bookName, user, System.currentTimeMillis());
    }
    
    @Override
    public void logBookDeleted(String bookName, String user) {
        businessLogger.warn("BOOK_DELETED | Book: {} | User: {} | Timestamp: {}", 
            bookName, user, System.currentTimeMillis());
    }
    
    @Override
    public void logBookViewed(String bookName, String user) {
        businessLogger.debug("BOOK_VIEWED | Book: {} | User: {} | Timestamp: {}", 
            bookName, user, System.currentTimeMillis());
    }
    
    // Order operations
    @Override
    public void logOrderCreated(String clientEmail, String totalAmount) {
        businessLogger.info("ORDER_CREATED | OrderId: {} | Client: {} | Amount: {} | Timestamp: {}", 
            clientEmail, totalAmount, System.currentTimeMillis());
    }
    
    @Override
    public void logOrderCompleted(String orderId, String clientEmail) {
        businessLogger.info("ORDER_COMPLETED | OrderId: {} | Client: {} | Timestamp: {}", 
            orderId, clientEmail, System.currentTimeMillis());
    }
    
    @Override
    public void logOrderCancelled(String orderId, String clientEmail, String reason) {
        businessLogger.warn("ORDER_CANCELLED | OrderId: {} | Client: {} | Reason: {} | Timestamp: {}", 
            orderId, clientEmail, reason, System.currentTimeMillis());
    }
    
    // Cart operations
    @Override
    public void logItemAddedToCart(String bookName, String user, int quantity) {
        businessLogger.info("ITEM_ADDED_TO_CART | Book: {} | User: {} | Quantity: {} | Timestamp: {}", 
            bookName, user, quantity, System.currentTimeMillis());
    }
    
    @Override
    public void logItemRemovedFromCart(String bookName, String user) {
        businessLogger.info("ITEM_REMOVED_FROM_CART | Book: {} | User: {} | Timestamp: {}", 
            bookName, user, System.currentTimeMillis());
    }
    
    @Override
    public void logCartCleared(String user) {
        businessLogger.info("CART_CLEARED | User: {} | Timestamp: {}", 
            user, System.currentTimeMillis());
    }
    
    // User operations
    @Override
    public void logUserRegistered(String email, String role) {
        businessLogger.info("USER_REGISTERED | Email: {} | Role: {} | Timestamp: {}", 
            email, role, System.currentTimeMillis());
    }
    
    @Override
    public void logUserLogin(String email, boolean success) {
        if (success) {
            businessLogger.info("USER_LOGIN_SUCCESS | Email: {} | Timestamp: {}", 
                email, System.currentTimeMillis());
        } else {
            businessLogger.warn("USER_LOGIN_FAILED | Email: {} | Timestamp: {}", 
                email, System.currentTimeMillis());
        }
    }
    
    @Override
    public void logUserLogout(String email) {
        businessLogger.info("USER_LOGOUT | Email: {} | Timestamp: {}", 
            email, System.currentTimeMillis());
    }
    
    @Override
    public void logUserProfileUpdated(String email) {
        businessLogger.info("USER_PROFILE_UPDATED | Email: {} | Timestamp: {}", 
            email, System.currentTimeMillis());
    }
    
    // Search operations
    @Override
    public void logSearchPerformed(String searchTerm, String user, int resultsCount) {
        businessLogger.info("SEARCH_PERFORMED | Term: {} | User: {} | Results: {} | Timestamp: {}", 
            searchTerm, user, resultsCount, System.currentTimeMillis());
    }
    
    @Override
    public void logFilterApplied(String filterType, String filterValue, String user) {
        businessLogger.debug("FILTER_APPLIED | Type: {} | Value: {} | User: {} | Timestamp: {}", 
            filterType, filterValue, user, System.currentTimeMillis());
    }
    
    // General business events
    @Override
    public void logBusinessEvent(String eventType, String details, String user) {
        businessLogger.info("BUSINESS_EVENT | Type: {} | Details: {} | User: {} | Timestamp: {}", 
            eventType, details, user, System.currentTimeMillis());
    }
    
    @Override
    public void logPerformanceMetric(String operation, long durationMs) {
        performanceLogger.info("PERFORMANCE | Operation: {} | Duration: {}ms | Timestamp: {}", 
            operation, durationMs, System.currentTimeMillis());
    }
    
    @Override
    public void logDataAccess(String operation, String entity, boolean success) {
        if (success) {
            dataAccessLogger.debug("DATA_ACCESS_SUCCESS | Operation: {} | Entity: {} | Timestamp: {}", 
                operation, entity, System.currentTimeMillis());
        } else {
            dataAccessLogger.error("DATA_ACCESS_FAILED | Operation: {} | Entity: {} | Timestamp: {}", 
                operation, entity, System.currentTimeMillis());
        }
    }
}
