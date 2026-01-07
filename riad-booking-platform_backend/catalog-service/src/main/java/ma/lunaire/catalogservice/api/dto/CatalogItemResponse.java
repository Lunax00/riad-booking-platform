package ma.lunaire.catalogservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.lunaire.catalogservice.domain.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemResponse {

    private Long id;
    private String name;
    private String description;
    private String city;
    private String address;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private Integer numberOfRooms;
    private Integer numberOfBathrooms;
    private String amenities;
    private String imageUrl;
    private ItemStatus status;
    private String ownerId;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
