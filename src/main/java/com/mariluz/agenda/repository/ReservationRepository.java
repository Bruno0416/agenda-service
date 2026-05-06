package com.mariluz.agenda.repository;

import com.mariluz.agenda.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository
    extends JpaRepository<Reservation, Integer> {}
