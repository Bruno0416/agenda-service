package com.mariluz.agenda.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReservationResponse {

    // id reserva
    private Integer id;

    private LocalTime startTime;

    private LocalTime endTime;
}
