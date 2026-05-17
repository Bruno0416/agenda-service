/*
Entidad de configuracion de agenda-> sirve para autogenerar
todas las tuplas de AgendaSlot del mes
*/
package com.mariluz.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity(name = "agenda_config")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgendaConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalTime startWorkTime; // hora inicio de jornada

    @Column(nullable = false)
    private LocalTime endWorkTime; // hora fin de jornada

    @Column(nullable = false)
    @Positive(message = "La duracion del bloque debe ser numero positivo")
    @Min(value = 10, message = "El bloque debe durar minimo 10Min")
    private Integer slotDuration; // duracion del slot en minutoss

    @Setter(AccessLevel.NONE) // para no generar nada automaticamente
    @Column(nullable = false)
    @Builder.Default // para evitar que el deje sin inicializar la lista
    private List<Integer> workDays = new ArrayList<>(); // solo 1-7 | lo validamos con el dto

    @Column(nullable = false)
    private LocalDateTime updatedAt; // fecha/hora actualizacion
}
