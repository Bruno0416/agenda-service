package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.exceptions.UnauthorizedOperationException;
import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.model.User;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaConfigRepository agendaConfigRepo;

    // 1. configurar agenda (admin)
    @Override
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request) {
        // 1. Validar rol del usuario
        validateAdminAccess();
        // 2. guardar configuracion
        AgendaConfig agendaConfig = agendaConfigRepo.save(
            AgendaConfig.builder()
                .id(1) // guardamos con el id 1 para actualizar la configuracion y no crear una tupla nueva
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
            .workDays(agendaConfig.getWorkDays())
            .build();
    }

    /*
    2. generar agenda(admin) ----> solo genera la agenda para el mes
    ---> depende de 'https://api.boostr.cl/holidays.json' para encontrar dias feriados
    */

    private void createAgenda() {
        // 1. acceder a la configuracion de agenda
        Optional<AgendaConfig> configOpt = agendaConfigRepo.findById(1);
        if (configOpt.isEmpty()) {
            throw new RuntimeException();
        }

        AgendaConfig config = configOpt.get();

        // 2. calcular dias para generar horarios (int)
        LocalDate today = LocalDate.now();
        int days = today.lengthOfMonth() - today.getDayOfMonth();
        // 3. recorrer lista con los dias y filtrar los feriados
        for (int i = 0; i <= days; i++) {
            int day = today.plusDays(i).getDayOfWeek().getValue() - 1;
            System.out.printf(day + "\n");
        }
        // 4. crear los bloques horarios
        // 5. retornamos mensaje de exito
    }

    // 3. crear/agregar servicios (admin)

    // 4. reservar slot (cliente)

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

    private void validateAdminAccess() {
        User user = getCurrentUser();

        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            // si el usuario no es admin arrojamos un error
            throw new UnauthorizedOperationException(
                "Solo un administrador puede configurar la agenda"
            );
        }
    }
}
