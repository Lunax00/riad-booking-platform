package ma.lunaire.catalogservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String city;
    private Integer minCapacity;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}

