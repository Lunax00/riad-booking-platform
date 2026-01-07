package ma.lunaire.catalogservice.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.lunaire.catalogservice.domain.ItemStatus;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {

    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    private String city;

    private String address;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "La capacité doit être d'au moins 1 personne")
    private Integer capacity;

    @Min(value = 1, message = "Le nombre de chambres doit être d'au moins 1")
    private Integer numberOfRooms;

    @Min(value = 1, message = "Le nombre de salles de bain doit être d'au moins 1")
    private Integer numberOfBathrooms;

    private String amenities;

    private String imageUrl;

    private ItemStatus status;
}

