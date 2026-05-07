/*
Entidad de reservacion con informacion del usuario,
slot asignado e id del usuario
*/
package com.mariluz.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@Entity(name = "reservation")
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendaSlotId", nullable = false)
    private AgendaSlot agendaSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceId", nullable = false)
    private Services service;

    // TODO: revisar atributo
    @Column(nullable = false)
    private double estimatedPrice;

    // fecha y hora para saber la creacion de la reserva
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // fecha y hora para saber cuando se actualizo por ultima vez
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
