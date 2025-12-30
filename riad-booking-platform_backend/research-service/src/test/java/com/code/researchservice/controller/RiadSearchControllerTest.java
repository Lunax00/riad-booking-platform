package com.code.researchservice.controller;

import com.code.researchservice.dto.SearchCriteria;
import com.code.researchservice.model.Riad;
import com.code.researchservice.repository.RiadRepository;
import com.code.researchservice.service.RiadSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RiadSearchController.
 */
@ExtendWith(MockitoExtension.class)
class RiadSearchControllerTest {

    @Mock
    private RiadSearchService searchService;

    @Mock
    private RiadRepository riadRepository;

    @InjectMocks
    private RiadSearchController riadSearchController;

    private Riad riad;
    private SearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        riad = Riad.builder()
                .id("1")
                .nom("Riad Marrakech")
                .description("Beautiful traditional riad")
                .ville("Marrakech")
                .quartier("Medina")
                .capacite(6)
                .prixParNuit(150.0)
                .equipements(Arrays.asList("wifi", "piscine", "clim"))
                .disponibleDe(LocalDate.now())
                .disponibleA(LocalDate.now().plusMonths(6))
                .build();

        searchCriteria = SearchCriteria.builder()
                .ville("Marrakech")
                .quartier("Medina")
                .personnes(4)
                .prixMin(100.0)
                .prixMax(200.0)
                .build();
    }

    @Nested
    @DisplayName("Search Endpoint Tests")
    class SearchEndpointTests {

        @Test
        @DisplayName("Should return list of riads when search is successful")
        void shouldReturnListOfRiadsWhenSearchIsSuccessful() {
            // Given
            List<Riad> expectedRiads = Arrays.asList(riad);
            when(searchService.search(any(SearchCriteria.class))).thenReturn(expectedRiads);

            // When
            List<Riad> result = riadSearchController.search(searchCriteria);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNom()).isEqualTo("Riad Marrakech");
            assertThat(result.get(0).getVille()).isEqualTo("Marrakech");
            verify(searchService, times(1)).search(searchCriteria);
        }

        @Test
        @DisplayName("Should return empty list when no riads match criteria")
        void shouldReturnEmptyListWhenNoRiadsMatchCriteria() {
            // Given
            when(searchService.search(any(SearchCriteria.class))).thenReturn(Collections.emptyList());

            // When
            List<Riad> result = riadSearchController.search(searchCriteria);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(searchService, times(1)).search(searchCriteria);
        }

        @Test
        @DisplayName("Should return multiple riads when search matches multiple")
        void shouldReturnMultipleRiadsWhenSearchMatchesMultiple() {
            // Given
            Riad riad2 = Riad.builder()
                    .id("2")
                    .nom("Riad Gueliz")
                    .ville("Marrakech")
                    .quartier("Gueliz")
                    .capacite(8)
                    .prixParNuit(180.0)
                    .build();

            List<Riad> expectedRiads = Arrays.asList(riad, riad2);
            when(searchService.search(any(SearchCriteria.class))).thenReturn(expectedRiads);

            // When
            List<Riad> result = riadSearchController.search(searchCriteria);

            // Then
            assertThat(result).hasSize(2);
            verify(searchService, times(1)).search(searchCriteria);
        }

        @Test
        @DisplayName("Should search with date criteria")
        void shouldSearchWithDateCriteria() {
            // Given
            SearchCriteria criteriaWithDates = SearchCriteria.builder()
                    .ville("Marrakech")
                    .dateDebut(LocalDate.now().plusDays(1))
                    .dateFin(LocalDate.now().plusDays(5))
                    .build();

            when(searchService.search(any(SearchCriteria.class))).thenReturn(Arrays.asList(riad));

            // When
            List<Riad> result = riadSearchController.search(criteriaWithDates);

            // Then
            assertThat(result).hasSize(1);
            verify(searchService).search(criteriaWithDates);
        }

        @Test
        @DisplayName("Should search with equipements criteria")
        void shouldSearchWithEquipementsCriteria() {
            // Given
            SearchCriteria criteriaWithEquipements = SearchCriteria.builder()
                    .ville("Marrakech")
                    .equipements(Arrays.asList("wifi", "piscine"))
                    .build();

            when(searchService.search(any(SearchCriteria.class))).thenReturn(Arrays.asList(riad));

            // When
            List<Riad> result = riadSearchController.search(criteriaWithEquipements);

            // Then
            assertThat(result).hasSize(1);
            verify(searchService).search(criteriaWithEquipements);
        }

        @Test
        @DisplayName("Should search with minimal criteria")
        void shouldSearchWithMinimalCriteria() {
            // Given
            SearchCriteria minimalCriteria = SearchCriteria.builder()
                    .ville("Marrakech")
                    .build();

            when(searchService.search(any(SearchCriteria.class))).thenReturn(Arrays.asList(riad));

            // When
            List<Riad> result = riadSearchController.search(minimalCriteria);

            // Then
            assertThat(result).hasSize(1);
            verify(searchService).search(minimalCriteria);
        }

        @Test
        @DisplayName("Should search with price range criteria")
        void shouldSearchWithPriceRangeCriteria() {
            // Given
            SearchCriteria priceRangeCriteria = SearchCriteria.builder()
                    .prixMin(50.0)
                    .prixMax(300.0)
                    .build();

            when(searchService.search(any(SearchCriteria.class))).thenReturn(Arrays.asList(riad));

            // When
            List<Riad> result = riadSearchController.search(priceRangeCriteria);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPrixParNuit()).isBetween(50.0, 300.0);
            verify(searchService).search(priceRangeCriteria);
        }
    }

    @Nested
    @DisplayName("Index Endpoint Tests")
    class IndexEndpointTests {

        @Test
        @DisplayName("Should index riad successfully")
        void shouldIndexRiadSuccessfully() {
            // Given
            when(riadRepository.save(any(Riad.class))).thenReturn(riad);

            // When
            Riad result = riadSearchController.index(riad);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("1");
            assertThat(result.getNom()).isEqualTo("Riad Marrakech");
            verify(riadRepository, times(1)).save(riad);
        }

        @Test
        @DisplayName("Should index riad with all fields")
        void shouldIndexRiadWithAllFields() {
            // Given
            Riad fullRiad = Riad.builder()
                    .id("3")
                    .nom("Riad Luxe")
                    .description("Luxurious riad with all amenities")
                    .ville("Marrakech")
                    .quartier("Palmeraie")
                    .capacite(12)
                    .prixParNuit(500.0)
                    .equipements(Arrays.asList("wifi", "piscine", "spa", "restaurant"))
                    .disponibleDe(LocalDate.now())
                    .disponibleA(LocalDate.now().plusYears(1))
                    .build();

            when(riadRepository.save(any(Riad.class))).thenReturn(fullRiad);

            // When
            Riad result = riadSearchController.index(fullRiad);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("3");
            assertThat(result.getNom()).isEqualTo("Riad Luxe");
            assertThat(result.getCapacite()).isEqualTo(12);
            assertThat(result.getEquipements()).containsExactlyInAnyOrder("wifi", "piscine", "spa", "restaurant");
            verify(riadRepository).save(fullRiad);
        }

        @Test
        @DisplayName("Should update existing riad when indexing")
        void shouldUpdateExistingRiadWhenIndexing() {
            // Given
            Riad updatedRiad = Riad.builder()
                    .id("1")
                    .nom("Riad Marrakech Updated")
                    .ville("Marrakech")
                    .prixParNuit(200.0)
                    .build();

            when(riadRepository.save(any(Riad.class))).thenReturn(updatedRiad);

            // When
            Riad result = riadSearchController.index(updatedRiad);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("1");
            assertThat(result.getNom()).isEqualTo("Riad Marrakech Updated");
            assertThat(result.getPrixParNuit()).isEqualTo(200.0);
            verify(riadRepository).save(updatedRiad);
        }

        @Test
        @DisplayName("Should index new riad without id")
        void shouldIndexNewRiadWithoutId() {
            // Given
            Riad newRiad = Riad.builder()
                    .nom("New Riad")
                    .ville("Fes")
                    .quartier("Medina")
                    .capacite(4)
                    .prixParNuit(100.0)
                    .build();

            Riad savedRiad = Riad.builder()
                    .id("generated-id")
                    .nom("New Riad")
                    .ville("Fes")
                    .quartier("Medina")
                    .capacite(4)
                    .prixParNuit(100.0)
                    .build();

            when(riadRepository.save(any(Riad.class))).thenReturn(savedRiad);

            // When
            Riad result = riadSearchController.index(newRiad);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("generated-id");
            verify(riadRepository).save(newRiad);
        }
    }
}

