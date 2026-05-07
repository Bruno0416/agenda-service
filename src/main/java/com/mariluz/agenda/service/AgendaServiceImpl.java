package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.exceptions.UnauthorizedOperationException;
import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.model.User;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaConfigRepository agendaConfigRepo;

    @Override
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request) {
        // Validar rol del usuario
        validateAdminAcces();
        // 2. guardar configuracion
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

        // 3. retornar entidad guardada
        return AgendaConfigResponse.builder()
            .startWorkTime(agendaConfig.getStartWorkTime())
            .endWorkTime(agendaConfig.getEndWorkTime())
            .slotDuration(agendaConfig.getSlotDuration())
            .breakTime(agendaConfig.getBreakTime())
            .workDays(agendaConfig.getWorkDays())
            .build();
    }

    // ------------------ Helper para validar rol usuario -------------------
    private User getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new UnauthorizedOperationException(
                "No hay un usuario autenticado"
            );
        }

        return user;
    }

    private void validateAdminAcces() {
        User user = getCurrentUser();

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedOperationException(
                "Solo un administrador puede configurar la agenda"
            );
        }
    }
}
