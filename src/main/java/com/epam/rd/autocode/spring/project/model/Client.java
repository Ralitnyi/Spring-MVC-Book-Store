package com.epam.rd.autocode.spring.project.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CLIENTS")
@Data
@NoArgsConstructor
public class Client extends User {
        private BigDecimal balance;

        public Client(Long id, String email, String password, String name, BigDecimal balance) {
            super(id, email, password, name);
            this.balance = balance;
        }
}
