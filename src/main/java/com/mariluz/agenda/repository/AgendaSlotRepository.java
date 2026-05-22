package com.mariluz.agenda.repository;

import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.model.AgendaSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaSlotRepository
    extends JpaRepository<AgendaSlot, Integer>
{
    public Optional<AgendaSlot> findFirstByOrderByDateDesc();

    // query para obtener los bloques disponibles como dto
    @Query(
        "SELECT new com.mariluz.agenda.dto.SlotsResponse(" +
            "s.id, " +
            "s.date, " +
            "s.startTime, " +
            "s.endTime) " +
            "FROM agenda_slot s " +
            "WHERE s.date > CURRENT_DATE " +
            "AND s.isAvailable = true"
    )
    public List<SlotsResponse> getAllSlotsAsDTOs();

    // validacion del bloque antes de agendar la hora
    public boolean existsByIdAndIsAvailableTrueAndDateAfter(
        Integer id,
        LocalDate date
    );
}
