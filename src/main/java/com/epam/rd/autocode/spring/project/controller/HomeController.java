package com.epam.rd.autocode.spring.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final BusinessLoggingService businessLoggingService;
    private final ErrorLoggingService errorLoggingService;

    public HomeController(BusinessLoggingService businessLoggingService, ErrorLoggingService errorLoggingService) {
        this.businessLoggingService = businessLoggingService;
        this.errorLoggingService = errorLoggingService;
    }

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        try {
            logger.debug("Home page requested");
            businessLoggingService.logBusinessEvent("HOME_PAGE_REQUESTED", "User requested home page", "anonymous");
            return "index";
        } catch (Exception ex) {
            logger.error("Error loading home page", ex);
            errorLoggingService.logApplicationError("HOME_PAGE", ex, "anonymous");
            model.addAttribute("errorMessage", "Failed to load home page: " + ex.getMessage());
            return "error/error";
        }
    }
}
