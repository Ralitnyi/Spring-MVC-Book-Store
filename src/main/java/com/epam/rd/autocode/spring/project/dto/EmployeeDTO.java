package com.epam.rd.autocode.spring.project.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO{
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 255)
    private String password;
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String phone;
    private LocalDate birthDate;
}
