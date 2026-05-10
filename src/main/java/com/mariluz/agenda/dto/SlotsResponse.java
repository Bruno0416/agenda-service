package com.mariluz.agenda.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SlotsResponse {

    private Integer id;

    private LocalDate date;

    private String time;
}
