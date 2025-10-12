package com.epam.rd.autocode.spring.project.service;

import org.springframework.security.core.AuthenticationException;

public interface SecurityLoggingService {
    
    // Authentication events
    void logAuthenticationSuccess(String email, String ipAddress, String userAgent);
    void logAuthenticationFailure(String email, String reason, String ipAddress, String userAgent);
    void logLogout(String email, String ipAddress);
    void logSessionExpired(String email, String ipAddress);
    
    // Authorization events
    void logAccessGranted(String resource, String user, String ipAddress);
    void logAccessDenied(String resource, String user, String ipAddress, String reason);
    void logPrivilegeEscalation(String user, String fromRole, String toRole, String ipAddress);
    
    // Account events
    void logAccountCreated(String email, String role, String ipAddress);
    void logAccountLocked(String email, String reason, String ipAddress);
    void logAccountUnlocked(String email, String adminUser, String ipAddress);
    void logAccountDisabled(String email, String reason, String adminUser, String ipAddress);
    void logAccountEnabled(String email, String adminUser, String ipAddress);
    void logPasswordChanged(String email, String ipAddress);
    void logPasswordResetRequested(String email, String ipAddress);
    void logPasswordResetCompleted(String email, String ipAddress);
    
    // Security violations
    void logSuspiciousActivity(String user, String activity, String ipAddress, String details);
    void logMultipleFailedLogins(String email, int attempts, String ipAddress);
    void logBruteForceAttempt(String email, String ipAddress, int attempts);
    void logUnusualAccessPattern(String user, String resource, String ipAddress, String details);
    
    // Data access events
    void logSensitiveDataAccess(String user, String dataType, String operation, String ipAddress);
    void logDataExport(String user, String dataType, int recordCount, String ipAddress);
    void logDataModification(String user, String entity, String operation, String ipAddress);
    
    // System security events
    void logSecurityConfigurationChange(String adminUser, String setting, String oldValue, String newValue, String ipAddress);
    void logSecurityPolicyViolation(String user, String policy, String details, String ipAddress);
    void logSecurityScanResult(String scanType, boolean vulnerabilitiesFound, String details);
    
    // General security events
    void logSecurityEvent(String eventType, String user, String details, String ipAddress);
    void logSecurityAlert(String alertType, String severity, String message, String user, String ipAddress);
}
