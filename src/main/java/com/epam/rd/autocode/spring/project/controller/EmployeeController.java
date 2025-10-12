package com.epam.rd.autocode.spring.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	private EmployeeServiceImpl employeeService;

	public EmployeeController(EmployeeServiceImpl employeeService) {
		this.employeeService = employeeService;
	}
	
	@GetMapping("/profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String profile(Model model, Authentication authentication) {
        String email = authentication.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", employee);
        return "user/profile-employee";
    }
	
	@GetMapping("/edit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String getEditPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", employee);
        return "user/edit-employee";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String updateProfile(@ModelAttribute("employee") @Valid EmployeeDTO employeeDTO,
                               BindingResult bindingResult,
                               Model model,
                               Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "user/edit-employee";
        }

        String email = authentication.getName();
        employeeService.updateEmployeeByEmail(email, employeeDTO);
        return "redirect:/employee/profile";
    }
	
	@PostMapping("/delete")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public String deleteAccount(Authentication authentication, HttpServletRequest request) throws ServletException {
        employeeService.deleteEmployeeByEmail(authentication.getName());
        request.logout();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}