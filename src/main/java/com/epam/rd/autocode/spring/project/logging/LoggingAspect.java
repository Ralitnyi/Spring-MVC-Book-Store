package com.epam.rd.autocode.spring.project.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final BusinessLoggingService businessLoggingService;
    private final ErrorLoggingService errorLoggingService;
    
    public LoggingAspect(BusinessLoggingService businessLoggingService, 
                        ErrorLoggingService errorLoggingService) {
        this.businessLoggingService = businessLoggingService;
        this.errorLoggingService = errorLoggingService;
    }
    
    @Around("execution(* com.epam.rd.autocode.spring.project.controller.*.*(..)) && !execution(* com.epam.rd.autocode.spring.project.controller.CustomErrorController.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        
        logger.debug("Controller method started: {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            businessLoggingService.logPerformanceMetric(className + "." + methodName, duration);
            logger.debug("Controller method completed: {}.{} in {}ms", className, methodName, duration);
            
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            errorLoggingService.logApplicationError(className + "." + methodName, ex, "system");
            logger.error("Controller method failed: {}.{} in {}ms", className, methodName, duration, ex);
            throw ex;
        }
    }
    
    @Around("execution(* com.epam.rd.autocode.spring.project.service.*.*(..)) && !execution(* com.epam.rd.autocode.spring.project.service.impl.*LoggingServiceImpl.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        
        logger.debug("Service method started: {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            businessLoggingService.logPerformanceMetric(className + "." + methodName, duration);
            businessLoggingService.logDataAccess(className + "." + methodName, "service", true);
            logger.debug("Service method completed: {}.{} in {}ms", className, methodName, duration);
            
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            errorLoggingService.logApplicationError(className + "." + methodName, ex, "system");
            businessLoggingService.logDataAccess(className + "." + methodName, "service", false);
            logger.error("Service method failed: {}.{} in {}ms", className, methodName, duration, ex);
            throw ex;
        }
    }
    
    @Around("execution(* com.epam.rd.autocode.spring.project.repo.*.*(..)) && !execution(* com.epam.rd.autocode.spring.project.service.impl.*LoggingServiceImpl.*(..))")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        
        logger.debug("Repository method started: {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            businessLoggingService.logPerformanceMetric(className + "." + methodName, duration);
            businessLoggingService.logDataAccess(className + "." + methodName, "repository", true);
            logger.debug("Repository method completed: {}.{} in {}ms", className, methodName, duration);
            
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            errorLoggingService.logApplicationError(className + "." + methodName, ex, "system");
            businessLoggingService.logDataAccess(className + "." + methodName, "repository", false);
            logger.error("Repository method failed: {}.{} in {}ms", className, methodName, duration, ex);
            throw ex;
        }
    }
}