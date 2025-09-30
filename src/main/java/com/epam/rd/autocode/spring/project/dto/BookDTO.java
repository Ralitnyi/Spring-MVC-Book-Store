package com.epam.rd.autocode.spring.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String name;
    private String genre;
    private AgeGroup ageGroup;
    private BigDecimal price;
    private LocalDate publicationYear;
    private String author;
    private Integer numberOfPages;
    private String characteristics;
    private String description;
    private Language language;
}
