package com.mariluz.agenda.controller;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.service.AgendaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
@Validated
public class AgendaController implements AgendaApi {

    private final AgendaService service;

    // ----- Endpoints -----
    // 1. configurar agenda (admin)
    @Override
    @PostMapping("/config")
    public ResponseEntity<AgendaConfigResponse> configAgenda(
        @RequestBody AgendaConfigRequest request
    ) {
        return ResponseEntity.ok(service.configAgenda(request));
    }

    // 2. generar agenda (admin)
    @Override
    @PostMapping("/generate")
    public ResponseEntity<String> generateAgenda() {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.generateAgenda()
        );
    }

    // 3. ver horarios (cliente)
    @Override
    @GetMapping("/slots")
    public ResponseEntity<List<SlotsResponse>> listSlots() {
        return ResponseEntity.ok(service.listSlots());
    }

    // 4. reservar slot (cliente)
    @Override
    @PostMapping("/reservation/{slotId}")
    public ResponseEntity<ReservationResponse> createReservation(
        @PathVariable Integer slotId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.createReservation(slotId)
        );
    }

    // 5. mostrar reservas activas
    @Override
    @GetMapping("/my-reservations")
    public ResponseEntity<List<MyReservationResponse>> myReservations() {
        return ResponseEntity.ok(service.myReservations());
    }

    // 6. cancelar reserva
    @Override
    @DeleteMapping("/reservation/{id}")
    public ResponseEntity<CancellationResponse> cancelReservation(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.cancelReservation(id));
    }
}
