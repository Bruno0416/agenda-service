package com.mariluz.agenda.service;

import com.mariluz.agenda.client.NotificationClient;
import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.exceptions.InvalidAgendaSlotException;
import com.mariluz.agenda.exceptions.InvalidReservationException;
import com.mariluz.agenda.exceptions.InvalidWorkTimeException;
import com.mariluz.agenda.exceptions.SlotsAlreadyGeneratedException;
import com.mariluz.agenda.exceptions.UnauthenticatedException;
import com.mariluz.agenda.exceptions.UnauthorizedOperationException;
import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.model.AgendaSlot;
import com.mariluz.agenda.model.Reservation;
import com.mariluz.agenda.model.Status;
import com.mariluz.agenda.model.User;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import com.mariluz.agenda.repository.AgendaSlotRepository;
import com.mariluz.agenda.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {

    private final AgendaConfigRepository agendaConfigRepo;

    private final AgendaSlotRepository agendaSlotRepo;

    private final ReservationRepository reservationRepo;

    private final NotificationClient notiService;

    // ------------------ Helpers privados para validar rol usuario -------------------

    private User getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new UnauthenticatedException("No hay un usuario autenticado");
        }
        return user;
    }

    private void validateAdminAccess(String message) {
        User user = getCurrentUser();

        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            // si el usuario no es admin arrojamos un error
            throw new UnauthorizedOperationException(message);
        }
    }

    // 1. configurar agenda (admin)
    @Override
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request) {
        // 1. Validar rol del usuario
        validateAdminAccess("Solo un administrador puede configurar la agenda");
        // validar datos request
        if (!request.getStartWorkTime().isBefore(request.getEndWorkTime())) {
            throw new InvalidWorkTimeException(
                "La hora de termino de la jornada no puede ser antes de la hora inicio"
            );
        }
        // 2. guardar configuracion
        AgendaConfig agendaConfig = agendaConfigRepo.save(
            AgendaConfig.builder()
                // guardamos con el id 1 para actualizar la configuracion y no crear una tupla nueva
                .id(1)
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
    */
    @Override
    public String generateAgenda() {
        //------ Validar rol del usuario //------
        validateAdminAccess("Solo un administrador puede generar la agenda");

        // ----------- validar si ya se crearon los horarios para el mes antes de ejecutar -----------
        LocalDate today = LocalDate.now();

        // encontrar el ultimo registro de bloque horario | segun fecha
        Optional<AgendaSlot> lastSlot =
            agendaSlotRepo.findFirstByOrderByDateDesc();

        // si existen bloques y son despues de la fecha actual, no permitimos la creacion de nuevos bloques
        if (lastSlot.isPresent() && lastSlot.get().getDate().isAfter(today)) {
            LocalDate lastSlotDate = lastSlot.get().getDate();

            throw new SlotsAlreadyGeneratedException(
                "Los horarios para el mes ya han sido generados. Intente nuevamente despues del " +
                    lastSlotDate.format(
                        DateTimeFormatter.ofPattern(
                            "dd 'de' MMMM",
                            Locale.of("es")
                        )
                    )
            );
        }

        // 1. acceder a la configuracion de agenda
        Optional<AgendaConfig> configOpt = agendaConfigRepo.findById(1);
        if (configOpt.isEmpty()) {
            throw new RuntimeException(
                "No se encontró la configuración de agenda"
            );
        }

        AgendaConfig config = configOpt.get();

        // 2. calcular dias a generar simplificamos generando los siguientes 30 dias
        int daysToGenerate = 30;

        // 3. calcular bloques de horario completos
        Map<LocalTime, LocalTime> blocks = new LinkedHashMap<>(); // usamos LinkedHashMap para mantener el orden de insercion
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
            // lunes = 1, domingo = 7
            int dayValue = today.plusDays(i).getDayOfWeek().getValue();

            List<Integer> workDays = config.getWorkDays();
            // 2. ver si el valor esta en la lista

            if (workDays.contains(dayValue)) {
                // 3. crear horarios
                blocks.forEach((startTime, endTime) -> {
                    slotsToSave.add(
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

        return "Horario creado con exito.";
    }

    // 3. listar bloques horarios
    @Override
    public List<SlotsResponse> listSlots() {
        List<SlotsResponse> slots = agendaSlotRepo.getAllSlotsAsDTOs();
        return slots;
    }

    // 4. reservar slot (cliente)
    @Override
    @Transactional
    public ReservationResponse createReservation(Integer slotId) {
        // 1. sacar info usuario
        User user = getCurrentUser();
        // 2. validar disponibilidad bloque horario (isAvailable)

        if (
            !agendaSlotRepo.existsByIdAndIsAvailableTrueAndDateAfter(
                slotId,
                LocalDate.now()
            )
        ) {
            // si el bloque no esta disponible (es de una fecha anterior a la de ejecución o isAvailable = false) arrojamos un error
            throw new InvalidAgendaSlotException(
                "Bloque horario invalido. Verifique nuevamente el ID ingresado."
            );
        }
        // obtener slot
        AgendaSlot slot = agendaSlotRepo
            .findById(slotId)
            .orElseThrow(() ->
                new InvalidAgendaSlotException("Slot no encontrado")
            );
        // 3. crear reserva
        Reservation reservation = Reservation.builder()
            .userId(user.getId())
            .agendaSlot(slot)
            .status(Status.ACTIVE)
            .build();

        // guardar reserva
        reservationRepo.save(reservation);

        // actualizar estado slot
        slot.setAvailable(false);
        slot.setUpdatedAt(LocalDateTime.now());
        agendaSlotRepo.save(slot);

        // 3.5 mandar correo al usuario (email extraido por JWT)
        String email = user.getEmail();
        String title = "Reserva Confirmada";
        String message = "Tu cita ha sido confirmada.";

        try {
            notiService.sendReservationEmail(email, title, message, false);
        } catch (Exception e) {
            // usamos try/catch para evitar interrumpir la reserva si hay un error al enviar el correo
            System.out.println("Error: " + e.getMessage());
        }

        // 4. retornar hora creada
        return ReservationResponse.builder()
            .id(reservation.getId())
            .startTime(reservation.getAgendaSlot().getStartTime())
            .endTime(reservation.getAgendaSlot().getEndTime())
            .build();
    }

    // 5. mostrar slots reserva activos
    @Override
    public List<MyReservationResponse> myReservations() {
        // 1. obtener al usuario
        User user = getCurrentUser();
        // 2. obtener lista de reservas del usuario
        List<Reservation> reservations =
            reservationRepo.findByUserIdAndAgendaSlot_DateAfterAndStatus(
                user.getId(),
                LocalDate.now(),
                Status.ACTIVE
            );

        if (reservations.isEmpty()) {
            return new ArrayList<>();
        }
        // 3. transformar reservas a dtos
        List<MyReservationResponse> reservationsResponse = new ArrayList<>();
        reservations.forEach(r ->
            reservationsResponse.add(
                MyReservationResponse.builder()
                    .id(r.getId())
                    .date(r.getAgendaSlot().getDate())
                    .startTime(r.getAgendaSlot().getStartTime())
                    .endTime(r.getAgendaSlot().getEndTime())
                    .build()
            )
        );

        // 4. mostrar dto
        return reservationsResponse;
    }

    @Override
    public CancellationResponse cancelReservation(Integer resId) {
        // 1. encontrar y comprobar que existe la reserva (arrojar error en caso de que no exista)
        Reservation res = reservationRepo
            .findById(resId)
            .orElseThrow(() ->
                new InvalidReservationException(
                    "La reserva del ID ingresado no existe"
                )
            );

        // validar que la reserva pertenece al usuario autenticado
        User currentUser = getCurrentUser();
        if (!res.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException(
                "No tienes permiso para cancelar una reserva que no te pertenece"
            );
        }

        // validar que la reserva no este cancelada previamente
        if (res.getStatus() == Status.CANCELED) {
            throw new InvalidReservationException(
                "La reserva ya fue cancelada"
            );
        }

        // 2. cambiar status reserva
        res.setStatus(Status.CANCELED);
        // 2.1 guardar reserva actualizada (cancelada)
        reservationRepo.save(res);

        // 3. cambiar isAvailable a true para el bloque horario
        AgendaSlot slot = res.getAgendaSlot();
        slot.setAvailable(true);

        // 3.1 guardar bloque actualizado
        agendaSlotRepo.save(slot);

        // 4. retornar mensaje de exito
        return CancellationResponse.builder()
            .message("Reserva cancelada exitosamente")
            .build();
    }
}
