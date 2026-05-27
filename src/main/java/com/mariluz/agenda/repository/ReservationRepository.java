package com.mariluz.agenda.repository;

import com.mariluz.agenda.model.Reservation;
import com.mariluz.agenda.model.Status;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository
    extends JpaRepository<Reservation, Integer>
{
    // obtiene todas las reservas activas del usuario incluyendo las de hoy
    public List<Reservation> findByUserIdAndAgendaSlot_DateGreaterThanEqualAndStatus(
        String userId,
        LocalDate currentDate,
        Status status
    );
}
