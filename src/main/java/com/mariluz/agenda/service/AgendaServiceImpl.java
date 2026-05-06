package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaConfigRepository agendaConfigRepo;

    @Override
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request) {
        // 1. guardar configuracion
        AgendaConfig agendaConfig = agendaConfigRepo.save(
            AgendaConfig.builder()
                .id(1) // guardamos con el id 1 para no crear una nueva tupla
                .startWorkTime(request.getStartWorkTime())
                .endWorkTime(request.getEndWorkTime())
                .slotDuration(request.getSlotDuration())
                .breakTime(request.getBreakTime())
                .workDays(request.getWorkDays())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        // 2. retornar entidad guardada
        return AgendaConfigResponse.builder()
            .startWorkTime(agendaConfig.getStartWorkTime())
            .endWorkTime(agendaConfig.getEndWorkTime())
            .slotDuration(agendaConfig.getSlotDuration())
            .breakTime(agendaConfig.getBreakTime())
            .workDays(agendaConfig.getWorkDays())
            .build();
    }
}
