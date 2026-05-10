/*
 Entidad de bloque horario unico
*/

package com.mariluz.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "agenda_slot")
@AllArgsConstructor
@NoArgsConstructor
public class AgendaSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // fecha del slot
    @Column(nullable = false)
    private LocalDate date;

    // hora inicio
    @Column(nullable = false)
    private LocalTime startTime;

    // hora fin
    @Column(nullable = false)
    private LocalTime endTime;

    // esta disponible?
    @Column(nullable = false)
    private boolean isAvailable;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
