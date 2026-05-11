package com.mariluz.agenda.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class HolidayResponse {

    private String status;
    private List<Holiday> data;
}
