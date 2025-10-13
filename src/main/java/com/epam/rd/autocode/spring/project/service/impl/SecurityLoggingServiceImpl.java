package com.epam.rd.autocode.spring.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.service.SecurityLoggingService;

@Service
public class SecurityLoggingServiceImpl implements SecurityLoggingService {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger authLogger = LoggerFactory.getLogger("AUTH");
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger alertLogger = LoggerFactory.getLogger("ALERT");
    
    @Override
    public void logAuthenticationSuccess(String email, String ipAddress, String userAgent) {
        authLogger.info("AUTH_SUCCESS | Email: {} | IP: {} | UserAgent: {} | Timestamp: {}", 
            email, ipAddress, userAgent, System.currentTimeMillis());
    }
    
    @Override
    public void logAuthenticationFailure(String email, String reason, String ipAddress, String userAgent) {
        authLogger.warn("AUTH_FAILURE | Email: {} | Reason: {} | IP: {} | UserAgent: {} | Timestamp: {}", 
            email, reason, ipAddress, userAgent, System.currentTimeMillis());
    }
    
    @Override
    public void logLogout(String email, String ipAddress) {
        authLogger.info("LOGOUT | Email: {} | IP: {} | Timestamp: {}", 
            email, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSessionExpired(String email, String ipAddress) {
        authLogger.warn("SESSION_EXPIRED | Email: {} | IP: {} | Timestamp: {}", 
            email, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccessGranted(String resource, String user, String ipAddress) {
        auditLogger.info("ACCESS_GRANTED | Resource: {} | User: {} | IP: {} | Timestamp: {}", 
            resource, user, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccessDenied(String resource, String user, String ipAddress, String reason) {
        securityLogger.warn("ACCESS_DENIED | Resource: {} | User: {} | IP: {} | Reason: {} | Timestamp: {}", 
            resource, user, ipAddress, reason, System.currentTimeMillis());
    }
    
    @Override
    public void logPrivilegeEscalation(String user, String fromRole, String toRole, String ipAddress) {
        securityLogger.warn("PRIVILEGE_ESCALATION | User: {} | From: {} | To: {} | IP: {} | Timestamp: {}", 
            user, fromRole, toRole, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccountCreated(String email, String role, String ipAddress) {
        auditLogger.info("ACCOUNT_CREATED | Email: {} | Role: {} | IP: {} | Timestamp: {}", 
            email, role, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccountLocked(String email, String reason, String ipAddress) {
        securityLogger.warn("ACCOUNT_LOCKED | Email: {} | Reason: {} | IP: {} | Timestamp: {}", 
            email, reason, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccountUnlocked(String email, String adminUser, String ipAddress) {
        auditLogger.info("ACCOUNT_UNLOCKED | Email: {} | Admin: {} | IP: {} | Timestamp: {}", 
            email, adminUser, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccountDisabled(String email, String reason, String adminUser, String ipAddress) {
        securityLogger.warn("ACCOUNT_DISABLED | Email: {} | Reason: {} | Admin: {} | IP: {} | Timestamp: {}", 
            email, reason, adminUser, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logAccountEnabled(String email, String adminUser, String ipAddress) {
        auditLogger.info("ACCOUNT_ENABLED | Email: {} | Admin: {} | IP: {} | Timestamp: {}", 
            email, adminUser, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logPasswordChanged(String email, String ipAddress) {
        auditLogger.info("PASSWORD_CHANGED | Email: {} | IP: {} | Timestamp: {}", 
            email, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logPasswordResetRequested(String email, String ipAddress) {
        auditLogger.info("PASSWORD_RESET_REQUESTED | Email: {} | IP: {} | Timestamp: {}", 
            email, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logPasswordResetCompleted(String email, String ipAddress) {
        auditLogger.info("PASSWORD_RESET_COMPLETED | Email: {} | IP: {} | Timestamp: {}", 
            email, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSuspiciousActivity(String user, String activity, String ipAddress, String details) {
        alertLogger.warn("SUSPICIOUS_ACTIVITY | User: {} | Activity: {} | IP: {} | Details: {} | Timestamp: {}", 
            user, activity, ipAddress, details, System.currentTimeMillis());
    }
    
    @Override
    public void logMultipleFailedLogins(String email, int attempts, String ipAddress) {
        alertLogger.warn("MULTIPLE_FAILED_LOGINS | Email: {} | Attempts: {} | IP: {} | Timestamp: {}", 
            email, attempts, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logBruteForceAttempt(String email, String ipAddress, int attempts) {
        alertLogger.error("BRUTE_FORCE_ATTEMPT | Email: {} | IP: {} | Attempts: {} | Timestamp: {}", 
            email, ipAddress, attempts, System.currentTimeMillis());
    }
    
    @Override
    public void logUnusualAccessPattern(String user, String resource, String ipAddress, String details) {
        alertLogger.warn("UNUSUAL_ACCESS_PATTERN | User: {} | Resource: {} | IP: {} | Details: {} | Timestamp: {}", 
            user, resource, ipAddress, details, System.currentTimeMillis());
    }
    
    @Override
    public void logSensitiveDataAccess(String user, String dataType, String operation, String ipAddress) {
        auditLogger.info("SENSITIVE_DATA_ACCESS | User: {} | DataType: {} | Operation: {} | IP: {} | Timestamp: {}", 
            user, dataType, operation, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logDataExport(String user, String dataType, int recordCount, String ipAddress) {
        auditLogger.info("DATA_EXPORT | User: {} | DataType: {} | Records: {} | IP: {} | Timestamp: {}", 
            user, dataType, recordCount, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logDataModification(String user, String entity, String operation, String ipAddress) {
        auditLogger.info("DATA_MODIFICATION | User: {} | Entity: {} | Operation: {} | IP: {} | Timestamp: {}", 
            user, entity, operation, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSecurityConfigurationChange(String adminUser, String setting, String oldValue, String newValue, String ipAddress) {
        auditLogger.info("SECURITY_CONFIG_CHANGE | Admin: {} | Setting: {} | OldValue: {} | NewValue: {} | IP: {} | Timestamp: {}", 
            adminUser, setting, oldValue, newValue, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSecurityPolicyViolation(String user, String policy, String details, String ipAddress) {
        securityLogger.warn("SECURITY_POLICY_VIOLATION | User: {} | Policy: {} | Details: {} | IP: {} | Timestamp: {}", 
            user, policy, details, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSecurityScanResult(String scanType, boolean vulnerabilitiesFound, String details) {
        if (vulnerabilitiesFound) {
            alertLogger.warn("SECURITY_SCAN_VULNERABILITIES | ScanType: {} | Details: {} | Timestamp: {}", 
                scanType, details, System.currentTimeMillis());
        } else {
            securityLogger.info("SECURITY_SCAN_CLEAN | ScanType: {} | Timestamp: {}", 
                scanType, System.currentTimeMillis());
        }
    }
    
    @Override
    public void logSecurityEvent(String eventType, String user, String details, String ipAddress) {
        securityLogger.info("SECURITY_EVENT | Type: {} | User: {} | Details: {} | IP: {} | Timestamp: {}", 
            eventType, user, details, ipAddress, System.currentTimeMillis());
    }
    
    @Override
    public void logSecurityAlert(String alertType, String severity, String message, String user, String ipAddress) {
        alertLogger.error("SECURITY_ALERT | Type: {} | Severity: {} | Message: {} | User: {} | IP: {} | Timestamp: {}", 
            alertType, severity, message, user, ipAddress, System.currentTimeMillis());
    }
}
