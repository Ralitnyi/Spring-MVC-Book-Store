package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.OnCreate;
import com.epam.rd.autocode.spring.project.dto.OnUpdate;
import com.epam.rd.autocode.spring.project.service.BusinessLoggingService;
import com.epam.rd.autocode.spring.project.service.ErrorLoggingService;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/client")
@SessionAttributes("cart")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    
    private final ClientServiceImpl clientService;
    private final BusinessLoggingService businessLoggingService;
    private final ErrorLoggingService errorLoggingService;

    public ClientController(ClientServiceImpl clientService, BusinessLoggingService businessLoggingService, ErrorLoggingService errorLoggingService) {
        this.clientService = clientService;
        this.businessLoggingService = businessLoggingService;
        this.errorLoggingService = errorLoggingService;
    }

    @ModelAttribute("cart")
    public Map<String, Integer> cart() {
        return new HashMap<>();
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, 
                              @RequestParam(value = "error", required = false) Boolean error,
                              @RequestParam(value = "blocked", required = false) Boolean blocked) {
        model.addAttribute("client", new ClientDTO());
        if (error != null && error) {
            model.addAttribute("loginError", "Invalid email or password");
        }
        if (blocked != null && blocked) {
            model.addAttribute("loginError", "Your account has been blocked. Please contact support.");
        }
        return "user/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("client", new ClientDTO());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("client") @Validated(OnCreate.class) ClientDTO clientDTO,
                           BindingResult bindingResult,
                           Model model) {
        try {
            logger.debug("Client registration attempt for email: {}", clientDTO.getEmail());
            businessLoggingService.logBusinessEvent("CLIENT_REGISTRATION_ATTEMPT", "Client registration attempt: " + clientDTO.getEmail(), "anonymous");
            
            if (bindingResult.hasErrors()) {
                logger.warn("Validation errors in client registration: {}", bindingResult.getAllErrors());
                errorLoggingService.logValidationError("client", clientDTO.toString(), "Validation failed", "anonymous");
                return "user/register";
            }

            clientDTO.setBalance(BigDecimal.ZERO);
            clientService.addClient(clientDTO);
            logger.info("Successfully registered client: {}", clientDTO.getEmail());
            businessLoggingService.logUserRegistered(clientDTO.getEmail(), "CLIENT");
            return "redirect:/client/login";
        } catch (Exception ex) {
            logger.error("Error during client registration: {}", clientDTO.getEmail(), ex);
            errorLoggingService.logApplicationError("CLIENT_REGISTRATION", ex, "anonymous");
            model.addAttribute("errorMessage", "Failed to register: " + ex.getMessage());
            return "user/register";
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String profile(Model model, Authentication authentication) {
        String email = authentication.getName();
        ClientDTO client = clientService.getClientByEmail(email);
        model.addAttribute("client", client);
        return "user/profile-client";
    }

    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String getEditPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        ClientDTO client = clientService.getClientByEmail(email);
        model.addAttribute("client", client);
        return "user/edit-client";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String updateProfile(@ModelAttribute("client") @Validated(OnUpdate.class) ClientDTO clientDTO,
                               BindingResult bindingResult,
                               Model model,
                               Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "user/edit-client";
        }

        String email = authentication.getName();
        clientService.updateClientByEmail(email, clientDTO);
        return "redirect:/client/profile";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String deleteAccount(Authentication authentication, HttpServletRequest request) throws ServletException {
        clientService.deleteClientByEmail(authentication.getName());
        request.logout();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}