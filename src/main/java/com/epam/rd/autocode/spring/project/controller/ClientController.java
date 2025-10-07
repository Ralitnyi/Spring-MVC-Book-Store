package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientController {

    private final ClientServiceImpl clientService;

    public ClientController(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, Boolean error) {
        model.addAttribute("client", new ClientDTO());
        if (error != null && error) {
            model.addAttribute("loginError", "Invalid email or password");
        }
        return "user/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("client", new ClientDTO());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("client") @Valid ClientDTO clientDTO,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        clientService.addClient(clientDTO);
        return "redirect:/login";
    }
}
