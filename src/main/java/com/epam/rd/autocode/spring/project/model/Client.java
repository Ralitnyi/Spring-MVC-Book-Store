package com.epam.rd.autocode.spring.project.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CLIENTS")
@Data
@NoArgsConstructor
public class Client extends User {
        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 10, fraction = 2)
        private BigDecimal balance;

        public Client(Long id, String email, String password, String name, BigDecimal balance, String role, Boolean blocked) {
            super(id, email, password, name, role, blocked);
            this.balance = balance;
        }
}