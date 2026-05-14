package com.mariluz.agenda.controller;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.service.AgendaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaService service;

    // ----- Endpoints -----
    // 1. configurar agenda (admin)
    @PostMapping("/config")
    public ResponseEntity<AgendaConfigResponse> configAgenda(
        @Valid @RequestBody AgendaConfigRequest request
    ) {
        return ResponseEntity.ok(service.configAgenda(request));
    }

    // 2. generar agenda(admin)
    @PostMapping("/generate")
    public ResponseEntity<String> generateAgenda() {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.generateAgenda()
        );
    }

    // 3. ver horarios (cliente)
    @GetMapping("/slots")
    public ResponseEntity<List<SlotsResponse>> listSlots() {
        return ResponseEntity.ok(service.listSlots());
    }

    // 4. reservar slot (cliente)
    @PostMapping("/reservation/{slotId}")
    public ResponseEntity<ReservationResponse> createReservation(
        @Valid @PathVariable Integer slotId
    ) {
        return ResponseEntity.ok(service.createReservation(slotId));
    }

    // 5. mostrar slots reserva activos
    @GetMapping("/my-reservations")
    public ResponseEntity<List<MyReservationResponse>> myReservations() {
        return ResponseEntity.ok(service.myReservations());
    }

    // 6. cancelar reserva
    @PostMapping("/cancel-reservation/{id}")
    public ResponseEntity<CancellationResponse> cancelReservation(
        @Valid @PathVariable Integer id
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
            service.cancelReservation(id)
        );
    }
}
