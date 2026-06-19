package com.mariluz.agenda.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.exceptions.InvalidReservationException;
import com.mariluz.agenda.exceptions.ReservationAlreadyCanceledException;
import com.mariluz.agenda.exceptions.SlotsAlreadyGeneratedException;
import com.mariluz.agenda.exceptions.UnauthenticatedException;
import com.mariluz.agenda.security.JwtUtil;
import com.mariluz.agenda.service.AgendaService;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AgendaController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtro JWT y seguridad para ejecutar el test
public class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper; // para mapear objetos/clases a json

    @MockitoBean
    private AgendaService service;

    @MockitoBean
    private JwtUtil jwtUtil; // importante para que funcione el service

    // -------------- 1. CONFIG --------------

    // 200
    @Test
    public void testConfigAgenda() throws Exception {
        // 1.  preparar request
        AgendaConfigRequest request = new AgendaConfigRequest();
        request.setStartWorkTime(LocalTime.of(9, 0));
        request.setEndWorkTime(LocalTime.of(17, 0));
        request.setSlotDuration(10);
        request.setWorkDays(List.of(1, 2, 3, 4, 5));

        // 2. preparar respuesta
        AgendaConfigResponse response = AgendaConfigResponse.builder()
            .startWorkTime(LocalTime.of(9, 0))
            .endWorkTime(LocalTime.of(17, 0))
            .slotDuration(10)
            .workDays(List.of(1, 2, 3, 4, 5))
            .build();

        // 3. configurar comportamiento del service
        when(service.configAgenda(request)).thenReturn(response);

        // 4. ejecutar request
        mockMvc
            .perform(
                post("/agenda/config")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
    }

    // 400
    @Test
    public void testCreateProductInvalidFields() throws Exception {
        // 1. preparar request prueba
        AgendaConfigRequest request = new AgendaConfigRequest();
        request.setStartWorkTime(LocalTime.of(19, 0));
        request.setEndWorkTime(LocalTime.of(9, 0));
        request.setSlotDuration(-12);
        request.setWorkDays(List.of(1, 2, 3, 5, 5));

        // 2. ejecutar test
        mockMvc
            .perform(
                post("/agenda/config")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    // 401
    @Test
    public void testCreateProductUnauthorized() throws Exception {
        // 1. preparar request prueba
        AgendaConfigRequest request = new AgendaConfigRequest();
        request.setStartWorkTime(LocalTime.of(9, 0));
        request.setEndWorkTime(LocalTime.of(17, 0));
        request.setSlotDuration(10);
        request.setWorkDays(List.of(1, 2, 3, 4, 5));

        // 2. ejecutar test
        when(service.configAgenda(request)).thenThrow(
            new UnauthenticatedException("No hay un usuario autenticado.")
        );

        mockMvc
            .perform(
                post("/agenda/config")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }

    // -------------- 2. GENERATE AGENDA --------------

    // Codigo 201
    @Test
    public void testGenerateAgenda() throws Exception {
        mockMvc
            .perform(post("/agenda/generate"))
            .andExpect(status().isCreated());
    }

    // Codigo 401
    @Test
    public void testGenerateAgendaUnauthorized() throws Exception {
        doThrow(new UnauthenticatedException("No hay un usuario autenticado."))
            .when(service)
            .generateAgenda();

        mockMvc
            .perform(post("/agenda/generate"))
            .andExpect(status().isUnauthorized());
    }

    // Codigo 409
    @Test
    public void testGenerateAgendaConflict() throws Exception {
        doThrow(new SlotsAlreadyGeneratedException(""))
            .when(service)
            .generateAgenda();

        mockMvc
            .perform(post("/agenda/generate"))
            .andExpect(status().isConflict());
    }

    // -------------- 3. SLOTS --------------

    // 200
    @Test
    public void testListSlots() throws Exception {
        mockMvc.perform(get("/agenda/slots")).andExpect(status().isOk());
    }

    // -------------- 4. RESERVATION --------------

    // 201
    @Test
    public void testCreateReservation() throws Exception {
        // 1. preparar response
        ReservationResponse response = ReservationResponse.builder()
            .id(1)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        // 2. configurar comportamiento del service
        when(service.createReservation(1)).thenReturn(response);

        mockMvc
            .perform(post("/agenda/reservation/1"))
            .andExpect(status().isCreated());
    }

    // 400
    @Test
    public void testCreateReservationInvalidSlot() throws Exception {
        // 1. preparar request
        Integer invalidSlotId = -1;

        // 2. ejecutar request
        mockMvc
            .perform(post("/agenda/reservation/" + invalidSlotId))
            .andExpect(status().isBadRequest());
    }

    // -------------- 5. MY RESERVATIONS --------------
    // 200
    @Test
    public void testMyReservations() throws Exception {
        // 1. preparar response
        MyReservationResponse response = MyReservationResponse.builder()
            .id(1)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        // 2. configurar comportamiento del service
        when(service.myReservations()).thenReturn(List.of(response));

        mockMvc
            .perform(get("/agenda/my-reservations"))
            .andExpect(status().isOk());
    }

    // -------------- 6. CANCEL RESERVATION --------------
    // 200
    @Test
    public void testCancelReservation() throws Exception {
        // 1. preparar response
        CancellationResponse response = CancellationResponse.builder()
            .message("Reserva cancelada")
            .build();

        // 2. configurar comportamiento del service
        when(service.cancelReservation(1)).thenReturn(response);

        mockMvc
            .perform(delete("/agenda/reservation/1"))
            .andExpect(status().isOk());
    }

    // 400
    @Test
    public void testCancelReservationInvalidId() throws Exception {
        // 1. preparar response
        CancellationResponse response = CancellationResponse.builder()
            .message("Reserva cancelada")
            .build();

        // 2. configurar comportamiento del service
        when(service.cancelReservation(-1)).thenReturn(response);

        mockMvc
            .perform(delete("/agenda/reservation/-1"))
            .andExpect(status().isBadRequest());
    }

    // 404
    @Test
    public void testCancelReservationIdNotFound() throws Exception {
        when(service.cancelReservation(99)).thenThrow(
            new InvalidReservationException("")
        );

        mockMvc
            .perform(delete("/agenda/reservation/99"))
            .andExpect(status().isNotFound());
    }

    // 409
    @Test
    public void testCancelReservationConflict() throws Exception {
        when(service.cancelReservation(99)).thenThrow(
            new ReservationAlreadyCanceledException("")
        );

        mockMvc
            .perform(delete("/agenda/reservation/99"))
            .andExpect(status().isConflict());
    }
}
