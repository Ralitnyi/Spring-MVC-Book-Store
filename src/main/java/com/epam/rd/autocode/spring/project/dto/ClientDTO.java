package com.epam.rd.autocode.spring.project.dto;


import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO{
    	@NotNull
    	@NotBlank
    	@Email
    private String email;
    	@NotNull(groups = OnCreate.class)
    	@NotBlank(groups = OnCreate.class)
    	@Size(min = 6, max = 255, groups = OnCreate.class)
    private String password;
    	@NotNull
    	@NotBlank
    	@Size(max = 255)
    private String name;
    private BigDecimal balance;
    private boolean blocked;
}
