package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BlockedUserInterceptor implements HandlerInterceptor {

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            String email = authentication.getName();
            
            Client client = clientRepository.findByEmail(email);
            if (client != null && client.isBlocked()) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                response.sendRedirect("/client/login?blocked=true");
                return false;
            }
            
            Employee employee = employeeRepository.findByEmail(email);
            if (employee != null && employee.isBlocked()) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                response.sendRedirect("/client/login?blocked=true");
                return false;
            }
        }
        
        return true;
    }
}
