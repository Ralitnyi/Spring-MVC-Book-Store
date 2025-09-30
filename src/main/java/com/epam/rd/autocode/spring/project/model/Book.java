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
    private String name;
	
    private String genre;
    
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;
    
    private BigDecimal price;
    private LocalDate publicationYear;
    private String author;
    private Integer numberOfPages;
    private String characteristics;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Language language;
    
}