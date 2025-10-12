package com.epam.rd.autocode.spring.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO{    
	
	@NotNull
	private Long id;
	
    @NotNull
    @Email
    @Size(max = 255)
    private String clientEmail;
    
    @Email
    @Size(max = 255)
    private String employeeEmail;
    
    @NotNull
    @PastOrPresent
    private LocalDateTime orderDate;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
    
    @NotEmpty
    @Valid
    private List<BookItemDTO> bookItems;
    
    private boolean confirmed;
}
