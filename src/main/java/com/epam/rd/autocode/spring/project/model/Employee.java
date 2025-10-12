package com.epam.rd.autocode.spring.project.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EMPLOYEES")
@Data
@NoArgsConstructor
public class Employee extends User {
    	@NotBlank
    	@Size(max = 20)
    	@Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,20}$", message = "Invalid phone number format")
    	private String phone;
    	
    	@Past
    	private LocalDate birthDate;

        public Employee(Long id, String email, String password, String name, String phone, LocalDate birthDate, String role, Boolean blocked) {
            super(id, email, password, name, role, blocked);
            this.phone = phone;
            this.birthDate = birthDate;
        }
}
