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

    // rango horario reserva (ej: HH:mm - HH:mm)
    private LocalTime startTime;

    // mensaje para confirmar que la hora se envio al correo
    private LocalTime endTime;
}
