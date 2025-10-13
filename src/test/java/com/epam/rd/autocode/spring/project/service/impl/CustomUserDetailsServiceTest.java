package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Client client;
    private Employee employee;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail("client@test.com");
        client.setPassword("hashedPassword");
        client.setRole("CLIENT");
        client.setBlocked(false);

        employee = new Employee();
        employee.setEmail("employee@test.com");
        employee.setPassword("hashedPassword");
        employee.setRole("EMPLOYEE");
        employee.setBlocked(false);
    }

    @Test
    void testLoadUserByUsername_ClientFound() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("client@test.com");

        assertEquals("client@test.com", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
        assertTrue(userDetails.isAccountNonLocked());
        verify(employeeRepository, never()).findByEmail(any());
    }

    @Test
    void testLoadUserByUsername_EmployeeFound() {
        when(clientRepository.findByEmail("employee@test.com")).thenReturn(null);
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(employee);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("employee@test.com");

        assertEquals("employee@test.com", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testLoadUserByUsername_ClientBlocked() {
        client.setBlocked(true);
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("client@test.com");

        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void testLoadUserByUsername_EmployeeBlocked() {
        employee.setBlocked(true);
        when(clientRepository.findByEmail("employee@test.com")).thenReturn(null);
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(employee);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("employee@test.com");

        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);
        when(employeeRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername("nonexistent@test.com")
        );

        assertEquals("User not found with email: nonexistent@test.com", exception.getMessage());
    }
}
