package com.epam.rd.autocode.spring.project.service;

import java.util.List;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee);

    void deleteEmployeeByEmail(String email);

    EmployeeDTO addEmployee(EmployeeDTO employee);
}
