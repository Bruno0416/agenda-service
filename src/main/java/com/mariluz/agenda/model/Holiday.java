package com.mariluz.agenda.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Holiday {

    private LocalDate date;

    private String title;

    private String type;

    private boolean inalienable;

    private String extra;
}
