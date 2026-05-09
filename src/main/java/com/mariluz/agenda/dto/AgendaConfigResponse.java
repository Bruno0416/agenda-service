package com.mariluz.agenda.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AgendaConfigResponse {

    private LocalTime startWorkTime;
    private LocalTime endWorkTime;
    private Integer slotDuration;
    private List<Integer> workDays;
}
