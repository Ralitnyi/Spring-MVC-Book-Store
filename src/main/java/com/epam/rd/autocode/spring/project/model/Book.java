package com.epam.rd.autocode.spring.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOOKS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	@NotNull
	@NotBlank
	@Size(max = 255)
    private String name;
	
	@Size(max = 255)
    private String genre;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private AgeGroup ageGroup;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
    
    @Column(name = "PUBLICATION_YEAR")
    @NotNull
    @PastOrPresent
    private LocalDate publicationDate;
    
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String author;
    
    @Column(name = "NUMBER_OF_PAGES")
    @NotNull
    @Positive
    private Integer pages;
    
    @Size(max = 2000)
    private String characteristics;
    
    @Size(max = 4000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;
}