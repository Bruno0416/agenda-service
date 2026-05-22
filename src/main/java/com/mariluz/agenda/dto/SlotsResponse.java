package com.mariluz.agenda.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SlotsResponse {

    private Integer id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
