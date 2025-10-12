package com.epam.rd.autocode.spring.project.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex, Model model) {
        logger.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/error";
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistException(AlreadyExistException ex, Model model) {
        logger.warn("Resource already exists: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "409");
        return "error/error";
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, Model model) {
        logger.warn("Validation error: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "400");
        return "error/error";
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleBusinessLogicException(BusinessLogicException ex, Model model) {
        logger.warn("Business logic error: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "422");
        return "error/error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        logger.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        logger.warn("Access denied: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Access denied. You don't have permission to access this resource.");
        model.addAttribute("errorCode", "403");
        return "error/error";
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException ex, Model model) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Authentication failed. Please check your credentials.");
        model.addAttribute("errorCode", "401");
        return "error/error";
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredentialsException(BadCredentialsException ex, Model model) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Invalid username or password.");
        model.addAttribute("errorCode", "401");
        return "error/error";
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleDisabledException(DisabledException ex, Model model) {
        logger.warn("Account disabled: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Your account has been disabled. Please contact administrator.");
        model.addAttribute("errorCode", "401");
        return "error/error";
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleLockedException(LockedException ex, Model model) {
        logger.warn("Account locked: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Your account has been locked. Please contact administrator.");
        model.addAttribute("errorCode", "401");
        return "error/error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex, Model model) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Data integrity violation. The operation cannot be completed.");
        model.addAttribute("errorCode", "409");
        return "error/error";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(DataAccessException ex, Model model) {
        logger.error("Database access error: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Database access error. Please try again later.");
        model.addAttribute("errorCode", "500");
        return "error/error";
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException ex, Model model) {
        logger.error("SQL error: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Database error. Please try again later.");
        model.addAttribute("errorCode", "500");
        return "error/error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", "Validation failed: " + ex.getMessage());
        modelAndView.addObject("errorCode", "400");
        modelAndView.setViewName("error/error");
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        logger.warn("No handler found for: {}", ex.getRequestURL());
        model.addAttribute("errorMessage", "Page not found: " + ex.getRequestURL());
        model.addAttribute("errorCode", "404");
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Invalid argument: " + ex.getMessage());
        model.addAttribute("errorCode", "400");
        return "error/error";
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        logger.warn("Illegal state: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Invalid state: " + ex.getMessage());
        model.addAttribute("errorCode", "409");
        return "error/error";
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleNullPointerException(NullPointerException ex, Model model) {
        logger.error("Null pointer exception: {}", ex.getMessage());
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", "500");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", "500");
        return "error/error";
    }
}
