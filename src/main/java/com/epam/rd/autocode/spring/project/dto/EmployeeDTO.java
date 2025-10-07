package com.epam.rd.autocode.spring.project.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO{
    private String email;
    private String password;
    private String name;
    private String phone;
    private LocalDate birthDate;
}
