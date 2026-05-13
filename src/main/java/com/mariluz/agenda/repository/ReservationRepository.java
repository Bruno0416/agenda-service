package com.mariluz.agenda.repository;

import com.mariluz.agenda.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository
    extends JpaRepository<Reservation, Integer>
{
    // obtiene todas las reservas (objeto completo con la relacion de agendaSlot) que le pertenezcan al usuario(userId) y sean despues de SYSDATE
    List<Reservation> findByUserIdAndAgendaSlot_DateAfter(
        String userId,
        LocalDate currentDate
    );
}
