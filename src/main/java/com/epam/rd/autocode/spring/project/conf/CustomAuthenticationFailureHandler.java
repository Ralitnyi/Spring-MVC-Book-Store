package com.epam.rd.autocode.spring.project.conf;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException, ServletException {
        String redirectUrl = "/client/login?error=true";
        
        if (exception instanceof LockedException || 
            exception instanceof DisabledException ||
            (exception instanceof AccountStatusException && 
             exception.getMessage().contains("locked"))) {
            redirectUrl = "/client/login?blocked=true";
        }
        
        response.sendRedirect(redirectUrl);
    }
}
