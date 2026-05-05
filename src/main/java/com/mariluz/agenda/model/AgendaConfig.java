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
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "agenda_config")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgendaConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Time StartWorkTime; // hora inicio de jornada

    @Column(nullable = false)
    private Time EndWorkTime; // hora fin de jornada

    @Column(nullable = false)
    private Integer SlotDurationMin; // duracion del slot en minutos

    @Column(nullable = false)
    private Integer BreakMin; //break en minutos

    @Setter(AccessLevel.NONE) // para no generar nada automaticamente
    @Column(nullable = false)
    private List<Integer> WorkDays; // solo 1-7 | lo validamos con el dto

    @Column(nullable = false)
    private LocalDateTime updatedAt; // fecha/hora actualizacion
}
