package com.code.reservationservice.mapper;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dto.CreateReservationRequest;
import com.code.reservationservice.dto.ReservationResponse;
import com.code.reservationservice.dto.UpdateReservationRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Reservation entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReservationMapper {

    /**
     * Convert CreateReservationRequest to Reservation entity.
     */
    Reservation toEntity(CreateReservationRequest request);

    /**
     * Convert Reservation entity to ReservationResponse DTO.
     */
    ReservationResponse toResponse(Reservation reservation);

    /**
     * Update existing Reservation from UpdateReservationRequest.
     * Only updates non-null fields.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateReservationRequest request, @MappingTarget Reservation reservation);
}


