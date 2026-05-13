package com.mariluz.agenda.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MyReservationResponse {

    private Integer id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
