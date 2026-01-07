package ma.lunaire.catalogservice.repository;

import ma.lunaire.catalogservice.domain.CatalogItem;
import ma.lunaire.catalogservice.domain.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CatalogRepository extends JpaRepository<CatalogItem, Long> {

    List<CatalogItem> findByStatus(ItemStatus status);

    List<CatalogItem> findByCity(String city);

    List<CatalogItem> findByCityIgnoreCase(String city);

    List<CatalogItem> findByOwnerId(String ownerId);

    Page<CatalogItem> findByStatus(ItemStatus status, Pageable pageable);

    @Query("SELECT c FROM CatalogItem c WHERE c.status = 'AVAILABLE' AND c.city LIKE %:city%")
    List<CatalogItem> findAvailableByCity(@Param("city") String city);

    @Query("SELECT c FROM CatalogItem c WHERE c.status = 'AVAILABLE' AND c.capacity >= :minCapacity")
    List<CatalogItem> findAvailableWithMinCapacity(@Param("minCapacity") Integer minCapacity);

    @Query("SELECT c FROM CatalogItem c WHERE c.status = 'AVAILABLE' AND c.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<CatalogItem> findAvailableByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT c FROM CatalogItem c WHERE c.status = 'AVAILABLE' " +
           "AND (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:minCapacity IS NULL OR c.capacity >= :minCapacity) " +
           "AND (:minPrice IS NULL OR c.pricePerNight >= :minPrice) " +
           "AND (:maxPrice IS NULL OR c.pricePerNight <= :maxPrice)")
    Page<CatalogItem> searchRiads(
            @Param("city") String city,
            @Param("minCapacity") Integer minCapacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT DISTINCT c.city FROM CatalogItem c WHERE c.status = 'AVAILABLE' ORDER BY c.city")
    List<String> findAllAvailableCities();
}
