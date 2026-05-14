package com.mariluz.agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CancellationResponse {

    private String message;
}
