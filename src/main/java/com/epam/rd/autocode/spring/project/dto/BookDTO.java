package com.epam.rd.autocode.spring.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String genre;

    @NotNull
    private AgeGroup ageGroup;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotNull
    @PastOrPresent
    private LocalDate publicationDate;

    @NotBlank
    @Size(max = 255)
    private String author;

    @NotNull
    @Positive
    private Integer pages;

    @Size(max = 2000)
    private String characteristics;

    @Size(max = 4000)
    private String description;

    @NotNull
    private Language language;
}
