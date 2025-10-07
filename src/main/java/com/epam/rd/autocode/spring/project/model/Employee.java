package com.epam.rd.autocode.spring.project.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EMPLOYEES")
@Data
@NoArgsConstructor
public class Employee extends User {
    	private String phone;
    	private LocalDate birthDate;

        public Employee(Long id, String email, String password, String name, String phone, LocalDate birthDate) {
            super(id, email, password, name);
            this.phone = phone;
            this.birthDate = birthDate;
        }
}
