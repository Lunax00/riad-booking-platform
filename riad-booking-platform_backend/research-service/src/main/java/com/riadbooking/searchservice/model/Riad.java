package com.riadbooking.searchservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "riads")
public class Riad {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String nom;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String ville; // ex: Marrakech

    @Field(type = FieldType.Keyword)
    private String quartier; // ex: Medina, Gueliz...

    @Field(type = FieldType.Integer)
    private Integer capacite; // nb personnes

    @Field(type = FieldType.Double)
    private Double prixParNuit;

    @Field(type = FieldType.Keyword)
    private List<String> equipements; // wifi, piscine, clim...

    // Disponibilité globale (simple)
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate disponibleDe;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate disponibleA;
}
