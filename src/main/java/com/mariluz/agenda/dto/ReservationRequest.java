package com.mariluz.agenda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReservationRequest {

    @NotNull(message = "El id no puede estar vacio")
    @Positive(message = "El id no puede ser negativo")
    private Integer slotId;
}
