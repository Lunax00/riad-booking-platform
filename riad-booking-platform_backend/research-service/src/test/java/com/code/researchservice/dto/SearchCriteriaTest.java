package com.code.researchservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SearchCriteria DTO.
 */
class SearchCriteriaTest {

    @Test
    @DisplayName("Should create SearchCriteria with builder")
    void shouldCreateSearchCriteriaWithBuilder() {
        // Given & When
        SearchCriteria criteria = SearchCriteria.builder()
                .ville("Marrakech")
                .quartier("Medina")
                .personnes(4)
                .prixMin(100.0)
                .prixMax(200.0)
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(5))
                .equipements(Arrays.asList("wifi", "piscine"))
                .build();

        // Then
        assertThat(criteria.getVille()).isEqualTo("Marrakech");
        assertThat(criteria.getQuartier()).isEqualTo("Medina");
        assertThat(criteria.getPersonnes()).isEqualTo(4);
        assertThat(criteria.getPrixMin()).isEqualTo(100.0);
        assertThat(criteria.getPrixMax()).isEqualTo(200.0);
        assertThat(criteria.getDateDebut()).isEqualTo(LocalDate.now());
        assertThat(criteria.getDateFin()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(criteria.getEquipements()).containsExactly("wifi", "piscine");
    }

    @Test
    @DisplayName("Should create empty SearchCriteria with NoArgsConstructor")
    void shouldCreateEmptySearchCriteriaWithNoArgsConstructor() {
        // Given & When
        SearchCriteria criteria = new SearchCriteria();

        // Then
        assertThat(criteria).isNotNull();
        assertThat(criteria.getVille()).isNull();
        assertThat(criteria.getQuartier()).isNull();
        assertThat(criteria.getPersonnes()).isNull();
    }

    @Test
    @DisplayName("Should create SearchCriteria with AllArgsConstructor")
    void shouldCreateSearchCriteriaWithAllArgsConstructor() {
        // Given
        List<String> equipements = Arrays.asList("wifi", "clim");
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(3);

        // When
        SearchCriteria criteria = new SearchCriteria(
                "Fes",
                "Medina",
                6,
                150.0,
                300.0,
                dateDebut,
                dateFin,
                equipements
        );

        // Then
        assertThat(criteria.getVille()).isEqualTo("Fes");
        assertThat(criteria.getQuartier()).isEqualTo("Medina");
        assertThat(criteria.getPersonnes()).isEqualTo(6);
        assertThat(criteria.getPrixMin()).isEqualTo(150.0);
        assertThat(criteria.getPrixMax()).isEqualTo(300.0);
        assertThat(criteria.getEquipements()).hasSize(2);
    }

    @Test
    @DisplayName("Should set and get values using setters")
    void shouldSetAndGetValuesUsingSetters() {
        // Given
        SearchCriteria criteria = new SearchCriteria();

        // When
        criteria.setVille("Casablanca");
        criteria.setQuartier("Corniche");
        criteria.setPersonnes(2);
        criteria.setPrixMin(50.0);
        criteria.setPrixMax(150.0);

        // Then
        assertThat(criteria.getVille()).isEqualTo("Casablanca");
        assertThat(criteria.getQuartier()).isEqualTo("Corniche");
        assertThat(criteria.getPersonnes()).isEqualTo(2);
        assertThat(criteria.getPrixMin()).isEqualTo(50.0);
        assertThat(criteria.getPrixMax()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        // Given
        SearchCriteria criteria1 = SearchCriteria.builder()
                .ville("Marrakech")
                .personnes(4)
                .build();

        SearchCriteria criteria2 = SearchCriteria.builder()
                .ville("Marrakech")
                .personnes(4)
                .build();

        // Then
        assertThat(criteria1).isEqualTo(criteria2);
        assertThat(criteria1.hashCode()).isEqualTo(criteria2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        // Given
        SearchCriteria criteria = SearchCriteria.builder()
                .ville("Marrakech")
                .personnes(4)
                .build();

        // Then
        assertThat(criteria.toString()).contains("ville=Marrakech");
        assertThat(criteria.toString()).contains("personnes=4");
    }

    @Test
    @DisplayName("Should handle null equipements list")
    void shouldHandleNullEquipementsList() {
        // Given & When
        SearchCriteria criteria = SearchCriteria.builder()
                .ville("Marrakech")
                .equipements(null)
                .build();

        // Then
        assertThat(criteria.getEquipements()).isNull();
    }

    @Test
    @DisplayName("Should handle empty equipements list")
    void shouldHandleEmptyEquipementsList() {
        // Given & When
        SearchCriteria criteria = SearchCriteria.builder()
                .equipements(Arrays.asList())
                .build();

        // Then
        assertThat(criteria.getEquipements()).isEmpty();
    }
}

