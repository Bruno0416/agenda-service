package com.mariluz.agenda.service;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;

public interface AgendaService {
    // 1. configurar agenda (admin)
    public AgendaConfigResponse configAgenda(AgendaConfigRequest request);
    // 2. generar agenda(admin)
    // 3. crear servicios (admin)
    // 4. reservar slot
}
