package com.mariluz.agenda.repository;

import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.model.AgendaSlot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaSlotRepository
    extends JpaRepository<AgendaSlot, Integer>
{
    Optional<AgendaSlot> findFirstByOrderByDateDesc();

    @Query(
        "SELECT new com.mariluz.agenda.dto.SlotsResponse(" +
            "s.id, " +
            "s.date, " +
            "CONCAT(FUNCTION('DATE_FORMAT', s.startTime, '%H:%i'), '-', FUNCTION('DATE_FORMAT', s.endTime, '%H:%i'))) " + // concatenamos hora inicio + hora fin
            "FROM agenda_slot s " +
            "WHERE s.date > CURRENT_DATE " + // agregamos where para que no devuelva slots anteriores a la fecha actual
            "AND s.isAvailable = true" // agregamos filtro para mostrar solo los horarios disponibles
    )
    List<SlotsResponse> getAllSlotsAsDTOs();
}
