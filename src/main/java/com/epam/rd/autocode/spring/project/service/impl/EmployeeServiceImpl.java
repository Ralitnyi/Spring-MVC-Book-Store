package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final PasswordEncoder passwordEncoder;

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EmployeeDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new NotFoundException("Employee not found: " + email);
        }
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employeeDTO) {
        Employee existing = employeeRepository.findByEmail(email);
        if (existing == null) {
            throw new NotFoundException("Employee not found: " + email);
        }
        existing.setEmail(employeeDTO.getEmail());
        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }
        existing.setName(employeeDTO.getName());
        existing.setPhone(employeeDTO.getPhone());
        existing.setBirthDate(employeeDTO.getBirthDate());
        Employee saved = employeeRepository.save(existing);
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployeeByEmail(String email) {
        if (!employeeRepository.existsByEmail(email)) {
            throw new NotFoundException("Employee not found: " + email);
        }
        employeeRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new AlreadyExistException("Employee already exists: " + employeeDTO.getEmail());
        }
        Employee entity = modelMapper.map(employeeDTO, Employee.class);
        if (entity.getRole() == null) {
        	entity.setRole("EMPLOYEE");
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        Employee saved = employeeRepository.save(entity);
        return modelMapper.map(saved, EmployeeDTO.class);
    }
}
