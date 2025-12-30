package com.code.researchservice.service;

import com.code.researchservice.dto.SearchCriteria;
import com.code.researchservice.model.Riad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RiadSearchService.
 */
@ExtendWith(MockitoExtension.class)
class RiadSearchServiceTest {

    @Mock
    private ElasticsearchOperations operations;

    @Mock
    private SearchHits<Riad> searchHits;

    @Mock
    private SearchHit<Riad> searchHit;

    @InjectMocks
    private RiadSearchService riadSearchService;

    private Riad riad;

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
                .equipements(Arrays.asList("wifi", "piscine"))
                .disponibleDe(LocalDate.now())
                .disponibleA(LocalDate.now().plusMonths(6))
                .build();
    }

    private void mockSearchHitsWithResults() {
        when(operations.search(any(CriteriaQuery.class), eq(Riad.class))).thenReturn(searchHits);
        when(searchHit.getContent()).thenReturn(riad);
        doAnswer(invocation -> {
            Consumer<SearchHit<Riad>> consumer = invocation.getArgument(0);
            consumer.accept(searchHit);
            return null;
        }).when(searchHits).forEach(any());
    }

    private void mockSearchHitsEmpty() {
        when(operations.search(any(CriteriaQuery.class), eq(Riad.class))).thenReturn(searchHits);
        doAnswer(invocation -> null).when(searchHits).forEach(any());
    }

    @Nested
    @DisplayName("Search with Ville criteria")
    class SearchWithVilleCriteria {

        @Test
        @DisplayName("Should search by ville")
        void shouldSearchByVille() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .ville("Marrakech")
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getVille()).isEqualTo("Marrakech");
            verify(operations).search(any(CriteriaQuery.class), eq(Riad.class));
        }
    }

    @Nested
    @DisplayName("Search with Quartier criteria")
    class SearchWithQuartierCriteria {

        @Test
        @DisplayName("Should search by quartier")
        void shouldSearchByQuartier() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .quartier("Medina")
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            verify(operations).search(any(CriteriaQuery.class), eq(Riad.class));
        }
    }

    @Nested
    @DisplayName("Search with Personnes criteria")
    class SearchWithPersonnesCriteria {

        @Test
        @DisplayName("Should search by minimum capacity")
        void shouldSearchByMinimumCapacity() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .personnes(4)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCapacite()).isGreaterThanOrEqualTo(4);
        }

        @Test
        @DisplayName("Should not add criteria when personnes is null")
        void shouldNotAddCriteriaWhenPersonnesIsNull() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .ville("Marrakech")
                    .personnes(null)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should not add criteria when personnes is zero or negative")
        void shouldNotAddCriteriaWhenPersonnesIsZeroOrNegative() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .personnes(0)
                    .build();

            mockSearchHitsEmpty();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search with Price criteria")
    class SearchWithPriceCriteria {

        @Test
        @DisplayName("Should search by prix min")
        void shouldSearchByPrixMin() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .prixMin(100.0)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPrixParNuit()).isGreaterThanOrEqualTo(100.0);
        }

        @Test
        @DisplayName("Should search by prix max")
        void shouldSearchByPrixMax() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .prixMax(200.0)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPrixParNuit()).isLessThanOrEqualTo(200.0);
        }

        @Test
        @DisplayName("Should search by price range")
        void shouldSearchByPriceRange() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .prixMin(100.0)
                    .prixMax(200.0)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search with Date criteria")
    class SearchWithDateCriteria {

        @Test
        @DisplayName("Should search by date range")
        void shouldSearchByDateRange() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .dateDebut(LocalDate.now().plusDays(1))
                    .dateFin(LocalDate.now().plusDays(5))
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should not add date criteria when only dateDebut is provided")
        void shouldNotAddDateCriteriaWhenOnlyDateDebutIsProvided() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .dateDebut(LocalDate.now().plusDays(1))
                    .dateFin(null)
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search with Equipements criteria")
    class SearchWithEquipementsCriteria {

        @Test
        @DisplayName("Should search by equipements")
        void shouldSearchByEquipements() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .equipements(Arrays.asList("wifi", "piscine"))
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should not add criteria when equipements is empty")
        void shouldNotAddCriteriaWhenEquipementsIsEmpty() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .equipements(List.of())
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search with combined criteria")
    class SearchWithCombinedCriteria {

        @Test
        @DisplayName("Should search with all criteria combined")
        void shouldSearchWithAllCriteriaCombined() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .ville("Marrakech")
                    .quartier("Medina")
                    .personnes(4)
                    .prixMin(100.0)
                    .prixMax(300.0)
                    .dateDebut(LocalDate.now().plusDays(1))
                    .dateFin(LocalDate.now().plusDays(5))
                    .equipements(Arrays.asList("wifi", "piscine"))
                    .build();

            mockSearchHitsWithResults();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).hasSize(1);
            verify(operations).search(any(CriteriaQuery.class), eq(Riad.class));
        }

        @Test
        @DisplayName("Should return empty list when no matches")
        void shouldReturnEmptyListWhenNoMatches() {
            // Given
            SearchCriteria criteria = SearchCriteria.builder()
                    .ville("NonExistentCity")
                    .build();

            mockSearchHitsEmpty();

            // When
            List<Riad> result = riadSearchService.search(criteria);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
