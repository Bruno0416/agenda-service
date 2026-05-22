package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import java.util.List;

public interface AgendaService {
    // 1. configurar agenda (admin)
    AgendaConfigResponse configAgenda(AgendaConfigRequest request);

    // 2. generar agenda(admin)
    String generateAgenda();

    // 3. listar horas
    List<SlotsResponse> listSlots();

    // 4. crear reserva
    ReservationResponse createReservation(Integer slotId);

    // 5. lista de reservas activas
    List<MyReservationResponse> myReservations();

    // 6. cancelar reserva
    CancellationResponse cancelReservation(Integer resId);
}
