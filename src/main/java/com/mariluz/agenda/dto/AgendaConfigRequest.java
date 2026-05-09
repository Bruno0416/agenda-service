package com.mariluz.agenda.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Builder
@AllArgsConstructor
public class AgendaConfigRequest {

    @NotNull(message = "La hora es obligatoria.")
    // para parsear el json a formato de hora automaticamente
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startWorkTime;

    @NotNull(message = "La hora es obligatoria.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endWorkTime;

    @NotNull(message = "la duracion del slot no puede estar vacia.")
    private Integer slotDuration;

    @NotEmpty(message = "Los dias de trabajo no pueden estar vacios.")
    @UniqueElements(message = "Los días de trabajo no pueden repetirse.")
    private List<
        @Min(
            value = 1,
            message = "El valor mínimo a ingresar debe ser 1 representando lunes"
        ) @Max(
            value = 7,
            message = "El valor máximo a ingresar debe ser 7 representando domingo"
        ) Integer
    > workDays;
}
