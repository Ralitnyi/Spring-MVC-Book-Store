package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmail("employee@test.com");
        employee.setName("Test Employee");
        employee.setPassword("encodedPassword");
        employee.setPhone("1234567890");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setRole("EMPLOYEE");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("employee@test.com");
        employeeDTO.setName("Test Employee");
        employeeDTO.setPassword("rawPassword");
        employeeDTO.setPhone("1234567890");
        employeeDTO.setBirthDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals(employeeDTO, result.get(0));
    }

    @Test
    void testGetEmployeeByEmail_Success() {
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeByEmail("employee@test.com");

        assertEquals(employeeDTO, result);
    }

    @Test
    void testGetEmployeeByEmail_NotFound() {
        when(employeeRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> employeeService.getEmployeeByEmail("nonexistent@test.com"));
    }

    @Test
    void testUpdateEmployeeByEmail_Success() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmail("updated@test.com");
        updatedEmployee.setName("Updated Employee");

        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setEmail("updated@test.com");
        updateDTO.setName("Updated Employee");
        updateDTO.setPassword("newPassword");
        updateDTO.setPhone("0987654321");
        updateDTO.setBirthDate(LocalDate.of(1995, 5, 5));

        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(employee);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(employeeRepository.save(employee)).thenReturn(updatedEmployee);
        when(modelMapper.map(updatedEmployee, EmployeeDTO.class)).thenReturn(updateDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail("employee@test.com", updateDTO);

        assertEquals(updateDTO, result);
        assertEquals("updated@test.com", employee.getEmail());
        assertEquals("encodedNewPassword", employee.getPassword());
        assertEquals("Updated Employee", employee.getName());
        assertEquals("0987654321", employee.getPhone());
        assertEquals(LocalDate.of(1995, 5, 5), employee.getBirthDate());
    }

    @Test
    void testUpdateEmployeeByEmail_NotFound() {
        when(employeeRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> employeeService.updateEmployeeByEmail("nonexistent@test.com", employeeDTO));
    }

    @Test
    void testDeleteEmployeeByEmail_Success() {
        when(employeeRepository.existsByEmail("employee@test.com")).thenReturn(true);

        employeeService.deleteEmployeeByEmail("employee@test.com");

        verify(employeeRepository).deleteByEmail("employee@test.com");
    }

    @Test
    void testDeleteEmployeeByEmail_NotFound() {
        when(employeeRepository.existsByEmail("nonexistent@test.com")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> employeeService.deleteEmployeeByEmail("nonexistent@test.com"));
    }

    @Test
    void testAddEmployee_Success() {
        Employee mappedEmployee = new Employee();
        mappedEmployee.setEmail("newemployee@test.com");
        mappedEmployee.setPassword("password123");
        mappedEmployee.setName("New Employee");

        Employee savedEmployee = new Employee();
        savedEmployee.setEmail("newemployee@test.com");
        savedEmployee.setPassword("encodedPassword");
        savedEmployee.setName("New Employee");
        savedEmployee.setRole("EMPLOYEE");

        EmployeeDTO newEmployeeDTO = new EmployeeDTO();
        newEmployeeDTO.setEmail("newemployee@test.com");
        newEmployeeDTO.setPassword("password123");
        newEmployeeDTO.setName("New Employee");

        when(employeeRepository.existsByEmail("newemployee@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(modelMapper.map(newEmployeeDTO, Employee.class)).thenReturn(mappedEmployee);
        when(employeeRepository.save(savedEmployee)).thenReturn(savedEmployee);
        when(modelMapper.map(savedEmployee, EmployeeDTO.class)).thenReturn(newEmployeeDTO);

        EmployeeDTO result = employeeService.addEmployee(newEmployeeDTO);

        assertEquals(newEmployeeDTO, result);
        assertEquals("EMPLOYEE", savedEmployee.getRole());
        assertEquals("encodedPassword", savedEmployee.getPassword());
        verify(employeeRepository).save(savedEmployee);
    }

    @Test
    void testAddEmployee_AlreadyExists() {
        when(employeeRepository.existsByEmail("employee@test.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(employeeDTO));
    }
}
