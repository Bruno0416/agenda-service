package com.mariluz.agenda.repository;

import com.mariluz.agenda.model.AgendaSlot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaSlotRepository
    extends JpaRepository<AgendaSlot, Integer>
{
    Optional<AgendaSlot> findFirstByOrderByDateDesc();
}
