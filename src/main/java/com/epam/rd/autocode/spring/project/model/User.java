package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@NotBlank
	@Email
	@Size(max = 255)
	private String email;
	
	@NotNull
	@NotBlank
	@Size(min = 6, max = 255)
	private String password;
	
	@NotNull
	@NotBlank
	@Size(max = 255)
	private String name;
	
	@NotNull
	@NotBlank
	@Size(max = 50)
    private String role;
    
    private boolean blocked;
}
