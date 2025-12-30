package com.code.researchservice.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Riad model.
 */
class RiadTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Riad with builder")
        void shouldCreateRiadWithBuilder() {
            // Given & When
            Riad riad = Riad.builder()
                    .id("1")
                    .nom("Riad Marrakech")
                    .description("Beautiful traditional riad")
                    .ville("Marrakech")
                    .quartier("Medina")
                    .capacite(6)
                    .prixParNuit(150.0)
                    .equipements(Arrays.asList("wifi", "piscine", "clim"))
                    .disponibleDe(LocalDate.of(2025, 1, 1))
                    .disponibleA(LocalDate.of(2025, 12, 31))
                    .build();

            // Then
            assertThat(riad.getId()).isEqualTo("1");
            assertThat(riad.getNom()).isEqualTo("Riad Marrakech");
            assertThat(riad.getDescription()).isEqualTo("Beautiful traditional riad");
            assertThat(riad.getVille()).isEqualTo("Marrakech");
            assertThat(riad.getQuartier()).isEqualTo("Medina");
            assertThat(riad.getCapacite()).isEqualTo(6);
            assertThat(riad.getPrixParNuit()).isEqualTo(150.0);
            assertThat(riad.getEquipements()).containsExactly("wifi", "piscine", "clim");
            assertThat(riad.getDisponibleDe()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(riad.getDisponibleA()).isEqualTo(LocalDate.of(2025, 12, 31));
        }

        @Test
        @DisplayName("Should create Riad with partial fields")
        void shouldCreateRiadWithPartialFields() {
            // Given & When
            Riad riad = Riad.builder()
                    .nom("Simple Riad")
                    .ville("Fes")
                    .prixParNuit(80.0)
                    .build();

            // Then
            assertThat(riad.getNom()).isEqualTo("Simple Riad");
            assertThat(riad.getVille()).isEqualTo("Fes");
            assertThat(riad.getPrixParNuit()).isEqualTo(80.0);
            assertThat(riad.getId()).isNull();
            assertThat(riad.getQuartier()).isNull();
        }
    }

    @Nested
    @DisplayName("NoArgsConstructor Tests")
    class NoArgsConstructorTests {

        @Test
        @DisplayName("Should create empty Riad with NoArgsConstructor")
        void shouldCreateEmptyRiadWithNoArgsConstructor() {
            // Given & When
            Riad riad = new Riad();

            // Then
            assertThat(riad).isNotNull();
            assertThat(riad.getId()).isNull();
            assertThat(riad.getNom()).isNull();
        }
    }

    @Nested
    @DisplayName("AllArgsConstructor Tests")
    class AllArgsConstructorTests {

        @Test
        @DisplayName("Should create Riad with AllArgsConstructor")
        void shouldCreateRiadWithAllArgsConstructor() {
            // Given
            List<String> equipements = Arrays.asList("wifi", "spa");
            LocalDate disponibleDe = LocalDate.of(2025, 1, 1);
            LocalDate disponibleA = LocalDate.of(2025, 6, 30);

            // When
            Riad riad = new Riad(
                    "2",
                    "Riad Luxe",
                    "Luxurious riad with spa",
                    "Marrakech",
                    "Palmeraie",
                    10,
                    400.0,
                    equipements,
                    disponibleDe,
                    disponibleA
            );

            // Then
            assertThat(riad.getId()).isEqualTo("2");
            assertThat(riad.getNom()).isEqualTo("Riad Luxe");
            assertThat(riad.getCapacite()).isEqualTo(10);
            assertThat(riad.getPrixParNuit()).isEqualTo(400.0);
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("Should set and get values using setters")
        void shouldSetAndGetValuesUsingSetters() {
            // Given
            Riad riad = new Riad();

            // When
            riad.setId("3");
            riad.setNom("Updated Riad");
            riad.setVille("Essaouira");
            riad.setCapacite(4);
            riad.setPrixParNuit(120.0);
            riad.setEquipements(Arrays.asList("wifi", "terrasse"));

            // Then
            assertThat(riad.getId()).isEqualTo("3");
            assertThat(riad.getNom()).isEqualTo("Updated Riad");
            assertThat(riad.getVille()).isEqualTo("Essaouira");
            assertThat(riad.getCapacite()).isEqualTo(4);
            assertThat(riad.getPrixParNuit()).isEqualTo(120.0);
            assertThat(riad.getEquipements()).containsExactly("wifi", "terrasse");
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            Riad riad1 = Riad.builder()
                    .id("1")
                    .nom("Riad Test")
                    .ville("Marrakech")
                    .build();

            Riad riad2 = Riad.builder()
                    .id("1")
                    .nom("Riad Test")
                    .ville("Marrakech")
                    .build();

            // Then
            assertThat(riad1).isEqualTo(riad2);
            assertThat(riad1.hashCode()).isEqualTo(riad2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Given
            Riad riad1 = Riad.builder()
                    .id("1")
                    .nom("Riad One")
                    .build();

            Riad riad2 = Riad.builder()
                    .id("2")
                    .nom("Riad Two")
                    .build();

            // Then
            assertThat(riad1).isNotEqualTo(riad2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            // Given
            Riad riad = Riad.builder()
                    .id("1")
                    .nom("Riad Test")
                    .ville("Marrakech")
                    .prixParNuit(150.0)
                    .build();

            // Then
            String toString = riad.toString();
            assertThat(toString).contains("id=1");
            assertThat(toString).contains("nom=Riad Test");
            assertThat(toString).contains("ville=Marrakech");
            assertThat(toString).contains("prixParNuit=150.0");
        }
    }

    @Nested
    @DisplayName("Equipements Tests")
    class EquipementsTests {

        @Test
        @DisplayName("Should handle multiple equipements")
        void shouldHandleMultipleEquipements() {
            // Given
            List<String> equipements = Arrays.asList("wifi", "piscine", "clim", "spa", "restaurant");

            // When
            Riad riad = Riad.builder()
                    .equipements(equipements)
                    .build();

            // Then
            assertThat(riad.getEquipements()).hasSize(5);
            assertThat(riad.getEquipements()).contains("wifi", "piscine", "clim", "spa", "restaurant");
        }

        @Test
        @DisplayName("Should handle null equipements")
        void shouldHandleNullEquipements() {
            // When
            Riad riad = Riad.builder()
                    .equipements(null)
                    .build();

            // Then
            assertThat(riad.getEquipements()).isNull();
        }
    }

    @Nested
    @DisplayName("Disponibilité Tests")
    class DisponibiliteTests {

        @Test
        @DisplayName("Should set availability dates")
        void shouldSetAvailabilityDates() {
            // Given
            LocalDate start = LocalDate.of(2025, 1, 1);
            LocalDate end = LocalDate.of(2025, 12, 31);

            // When
            Riad riad = Riad.builder()
                    .disponibleDe(start)
                    .disponibleA(end)
                    .build();

            // Then
            assertThat(riad.getDisponibleDe()).isEqualTo(start);
            assertThat(riad.getDisponibleA()).isEqualTo(end);
            assertThat(riad.getDisponibleDe()).isBefore(riad.getDisponibleA());
        }
    }
}

