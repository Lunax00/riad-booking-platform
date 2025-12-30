package com.code.researchservice.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {

    private String ville;       // Marrakech
    private String quartier;    // Medina, Gueliz...

    @Min(1)
    private Integer personnes;  // capacité min

    private Double prixMin;
    private Double prixMax;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private List<String> equipements; // ["wifi","piscine"]
}

