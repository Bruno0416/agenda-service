package com.mariluz.agenda.controller;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.service.AgendaService;
import jakarta.validation.Valid;
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

    // 3. crear servicios (admin)

    // 4. reservar slot (cliente)
}
