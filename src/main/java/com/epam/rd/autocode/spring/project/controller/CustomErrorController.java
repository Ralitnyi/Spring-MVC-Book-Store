package com.epam.rd.autocode.spring.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                logger.warn("404 error for URL: {}", request.getRequestURL());
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Page not found");
                return "error/error";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                logger.error("500 error for URL: {}", request.getRequestURL());
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Internal server error");
                return "error/error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                logger.warn("403 error for URL: {}", request.getRequestURL());
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "Access denied");
                return "error/access-denied";
            }
        }
        
        logger.warn("Unknown error for URL: {}", request.getRequestURL());
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "An unexpected error occurred");
        return "error/error";
    }
}
