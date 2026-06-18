package com.mariluz.agenda.controller;

import com.mariluz.agenda.dto.AgendaConfigRequest;
import com.mariluz.agenda.dto.AgendaConfigResponse;
import com.mariluz.agenda.dto.CancellationResponse;
import com.mariluz.agenda.dto.ErrorResponse;
import com.mariluz.agenda.dto.MyReservationResponse;
import com.mariluz.agenda.dto.ReservationResponse;
import com.mariluz.agenda.dto.SlotsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AgendaApi {
    // ----- Endpoints -----
    // 1. configurar agenda (admin)
    @Operation(
        summary = "Configurar agenda de trabajo",
        description = "Configura los parámetros de la agenda: horario de inicio/fin, duración de slots y días laborales. Solo accesible por administradores."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración guardada exitosamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AgendaConfigResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "startWorkTime": "09:00",
                        "endWorkTime": "18:00",
                        "slotDuration": 30,
                        "workDays": [1, 2, 3, 4, 5]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los campos enviados o en el rango horario (ej. hora de fin anterior a hora de inicio, duración de slot menor a 10 minutos).",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/config",
                        "errors": {
                            "slotDuration": "La duración mínima del slot es de 10 minutos.",
                            "workDays": "Los días de trabajo no pueden estar vacios."
                        },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/config",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para acceder a este recurso.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/config",
                        "errors": { "error": "Solo un administrador puede configurar la agenda" },
                        "message": "Debe ser administrador para realizar esta operacion",
                        "status": 403,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/config",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<AgendaConfigResponse> configAgenda(
        AgendaConfigRequest request
    );

    // 2. generar agenda (admin)
    @Operation(
        summary = "Generar bloques horarios de la agenda",
        description = "Genera los slots disponibles a partir de la configuración existente. Solo accesible por administradores."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Bloques horarios generados exitosamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "\"Agenda generada correctamente\""
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto. Los bloques horarios ya han sido generados previamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/generate",
                        "errors": { "error": "Los slots ya existen para el periodo configurado" },
                        "message": "Los bloques horarios ya han sido generados",
                        "status": 409,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/generate",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para acceder a este endpoint.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/generate",
                        "errors": { "error": "Solo un administrador puede realizar esta acción." },
                        "message": "Debe ser administrador para realizar esta operacion",
                        "status": 403,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/generate",
                        "errors": { "error": "No se encontró configuración de agenda" },
                        "message": "Error interno: configuracion de agenda no disponible",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<String> generateAgenda();

    // 3. ver horarios (cliente)
    @Operation(
        summary = "Listar horarios disponibles",
        description = "Retorna los slots de la agenda disponibles para reservar desde el día actual en adelante."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de horarios disponibles.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SlotsResponse.class),
                examples = @ExampleObject(
                    value = """
                    [
                        {
                            "id": 1,
                            "date": "2026-06-20",
                            "startTime": "09:00",
                            "endTime": "09:30"
                        },
                        {
                            "id": 2,
                            "date": "2026-06-20",
                            "startTime": "09:30",
                            "endTime": "10:00"
                        }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/slots",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/slots",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<List<SlotsResponse>> listSlots();

    // 4. reservar slot (cliente)
    @Operation(
        summary = "Reservar un horario",
        description = "Crea una reserva para el slot indicado. El slot debe estar disponible y el ID debe ser positivo."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Reserva creada exitosamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 10,
                        "startTime": "09:00",
                        "endTime": "09:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El slot indicado no está disponible (ya fue reservado o no existe) o el ID proporcionado no es válido.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/5",
                        "errors": { "error": "El slot no está disponible" },
                        "message": "Error al agendar -> horario invalido",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/{slotId}",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/{slotId}",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<ReservationResponse> createReservation(Integer slotId);

    // 5. mostrar reservas activas
    @Operation(
        summary = "Ver mis reservas activas",
        description = "Retorna las reservas activas del usuario autenticado desde el día actual en adelante."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de reservas activas del usuario.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MyReservationResponse.class),
                examples = @ExampleObject(
                    value = """
                    [
                        {
                            "id": 10,
                            "date": "2026-06-20",
                            "startTime": "09:00",
                            "endTime": "09:30"
                        },
                        {
                            "id": 11,
                            "date": "2026-06-22",
                            "startTime": "10:00",
                            "endTime": "10:30"
                        }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/my-reservations",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/my-reservations",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<List<MyReservationResponse>> myReservations();

    // 6. cancelar reserva
    @Operation(
        summary = "Cancelar una reserva",
        description = "Cancela la reserva indicada. No se pueden cancelar reservas del día actual ni de fechas pasadas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserva cancelada exitosamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CancellationResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "message": "Reserva cancelada exitosamente"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Reserva no encontrada con el ID proporcionado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/10",
                        "errors": { "error": "No existe reserva con id 10" },
                        "message": "Reserva no encontrada",
                        "status": 404,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto. La reserva ya fue cancelada previamente.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/10",
                        "errors": { "error": "La reserva ya se encuentra cancelada" },
                        "message": "La reserva ya fue cancelada",
                        "status": 409,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT inválido o expirado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/{id}",
                        "errors": { "error": "Token no válido o expirado" },
                        "message": "Error de autenticacion",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/agenda/reservation/{id}",
                        "errors": { "error": "Error inesperado" },
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<CancellationResponse> cancelReservation(Integer id);
}
