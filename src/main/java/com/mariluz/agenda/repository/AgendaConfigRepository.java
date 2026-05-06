package com.mariluz.agenda.repository;

import com.mariluz.agenda.model.AgendaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaConfigRepository
    extends JpaRepository<AgendaConfig, Integer> {}
