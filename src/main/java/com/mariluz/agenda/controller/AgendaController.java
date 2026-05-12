package com.mariluz.agenda.controller;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.ReservationRequest;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import com.mariluz.agenda.service.AgendaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok().body(service.configAgenda(request));
    }

    // 2. generar agenda(admin)
    @PostMapping("/generate")
    public ResponseEntity<?> generateAgenda() {
        service.generateAgenda();
        return ResponseEntity.ok().body("");
    }

    // 3. ver horarios (cliente)
    @GetMapping("/slots")
    public ResponseEntity<List<SlotsResponse>> listSlots() {
        return ResponseEntity.ok().body(service.listSlots());
    }

    // 4. reservar slot (cliente)
    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponse> createReservation(
        @Valid @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.ok().body(service.createReservation(request));
    }
}
