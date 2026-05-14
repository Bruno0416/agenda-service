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
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request);

    // 2. generar agenda(admin)
    public String generateAgenda();

    // 3. listar horas
    public List<SlotsResponse> listSlots();

    // 4. crear reserva
    public ReservationResponse createReservation(Integer slotId);

    // 5. lista de reservas activas
    public List<MyReservationResponse> myReservations();

    // 6. cancelar reserva
    public CancellationResponse cancelReservation(Integer resId);
}
