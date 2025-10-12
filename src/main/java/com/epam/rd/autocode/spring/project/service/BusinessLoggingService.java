package com.epam.rd.autocode.spring.project.service;

public interface BusinessLoggingService {
    
    // Book operations
    void logBookCreated(String bookName, String user);
    void logBookUpdated(String bookName, String user);
    void logBookDeleted(String bookName, String user);
    void logBookViewed(String bookName, String user);
    
    // Order operations
    void logOrderCreated(String clientEmail, String totalAmount);
    void logOrderCompleted(String orderId, String clientEmail);
    void logOrderCancelled(String orderId, String clientEmail, String reason);
    
    // Cart operations
    void logItemAddedToCart(String bookName, String user, int quantity);
    void logItemRemovedFromCart(String bookName, String user);
    void logCartCleared(String user);
    
    // User operations
    void logUserRegistered(String email, String role);
    void logUserLogin(String email, boolean success);
    void logUserLogout(String email);
    void logUserProfileUpdated(String email);
    
    // Search operations
    void logSearchPerformed(String searchTerm, String user, int resultsCount);
    void logFilterApplied(String filterType, String filterValue, String user);
    
    // General business events
    void logBusinessEvent(String eventType, String details, String user);
    void logPerformanceMetric(String operation, long durationMs);
    void logDataAccess(String operation, String entity, boolean success);
}
