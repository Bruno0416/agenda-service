package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.exceptions.UnauthorizedOperationException;
import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.model.AgendaSlot;
import com.mariluz.agenda.model.User;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import com.mariluz.agenda.repository.AgendaSlotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaConfigRepository agendaConfigRepo;

    @Autowired
    private AgendaSlotRepository agendaSlotRepo;

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
    @Override
    public void generateAgenda() {
        //Validar rol del usuario
        validateAdminAccess();
        // 1. acceder a la configuracion de agenda
        Optional<AgendaConfig> configOpt = agendaConfigRepo.findById(1);
        if (configOpt.isEmpty()) {
            throw new RuntimeException();
        }

        AgendaConfig config = configOpt.get();

        // 2. calcular dias para generar horarios (int)
        // LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate today = LocalDate.now();
        int daysToGenerate =
            today.plusDays(1).lengthOfMonth() - today.getDayOfMonth();

        // 3. calcular bloques de horario completos
        Map<LocalTime, LocalTime> blocks = new HashMap<>();
        LocalTime current = config.getStartWorkTime();
        LocalTime end = config.getEndWorkTime();
        Integer slotDuration = config.getSlotDuration();

        while (
            current.plusMinutes(slotDuration).isBefore(end) ||
            current.plusMinutes(slotDuration).equals(end)
        ) {
            // 1. calcular fin del rango e inicio del siguiente
            LocalTime next = current.plusMinutes(slotDuration);
            // 2. agregamos al Map
            blocks.put(current, next);
            // 3. next(siguiente) pasa a ser el current(actual)
            current = next;
        }

        // 3. recorrer lista con los dias | mas adelante -> filtrar los feriados
        List<AgendaSlot> slotsToSave = new ArrayList<>();
        for (int i = 0; i <= daysToGenerate; i++) {
            // 4. crear los bloques horarios

            LocalDate date = today.plusDays(i);
            // 1. obtener valor dia de la semana
            // lunes = 0, domingo = 6 | + 1 para que tenga los mismos valores que nuestro list
            int dayValue = today.plusDays(i).getDayOfWeek().getValue();

            List<Integer> workDays = config.getWorkDays();
            // 2. ver si el valor esta en la lista

            if (workDays.contains(dayValue)) {
                // 3. crear horarios
                blocks.forEach((startTime, endTime) -> {
                    slotsToSave.add(
                        //agendaSlotRepo.save(
                        AgendaSlot.builder()
                            .startTime(startTime)
                            .endTime(endTime)
                            .date(date)
                            .isAvailable(true)
                            .updatedAt(LocalDateTime.now())
                            .build()
                    );
                });
            }
        }

        agendaSlotRepo.saveAll(slotsToSave);
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
