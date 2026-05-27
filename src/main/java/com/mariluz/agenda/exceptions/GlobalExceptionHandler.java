package com.mariluz.agenda.exceptions;

import com.mariluz.agenda.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler Horario invalido al reservar
    @ExceptionHandler(InvalidAgendaSlotException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAgendaSlotException(
        InvalidAgendaSlotException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error al agendar -> horario invalido")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler Horario invalido
    @ExceptionHandler(InvalidWorkTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWorkTimeException(
        InvalidWorkTimeException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Horario invalido")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler reserva no encontrada
    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReservation(
        InvalidReservationException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message("Reserva no encontrada")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler reserva ya cancelada
    @ExceptionHandler(ReservationAlreadyCanceledException.class)
    public ResponseEntity<ErrorResponse> handleReservationAlreadyCanceled(
        ReservationAlreadyCanceledException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message("La reserva ya fue cancelada")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler configuracion de agenda no encontrada
    @ExceptionHandler(AgendaConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAgendaConfigNotFound(
        AgendaConfigNotFoundException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error interno: configuracion de agenda no disponible")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler bloques horarios
    @ExceptionHandler(SlotsAlreadyGeneratedException.class)
    public ResponseEntity<ErrorResponse> handleSlotsAlreadyGenerated(
        SlotsAlreadyGeneratedException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message("Los bloques horarios ya han sido generados")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler MethodNotSupported Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Método HTTP no permitido")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler usuario no autenticado
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticated(
        UnauthenticatedException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Usuario no autenticado")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler permisos usuario
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(
        UnauthorizedOperationException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .message("Debe ser administrador para realizar esta operacion")
                .errors(Map.of("error", ex.getMessage()))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler validacion de parametros de ruta y metodo
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
            errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validacion")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Global Handler | Validacion BindingResult ahora se corrobora aqui en el global handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validacion")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Validacion Parseo de hora (json)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(
        HttpMessageNotReadableException ex,
        HttpServletRequest request
    ) {
        Map<String, String> error = Map.of(
            "error",
            "Revise el formato de hora en los campos enviados. Formato correcto: HH:mm, Ej: 09:00"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Hora mal formateada")
                .errors(error)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler sin token / Spring Security (AuthenticationEntryPoint)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
        AuthenticationException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("No autenticado")
                .errors(Map.of("error", "Se requiere token de autenticación"))
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler token expirado
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredTokenException(
        ExpiredJwtException ex,
        HttpServletRequest request
    ) {
        Map<String, String> error = Map.of(
            "error",
            "El token ha expirado. Por favor, inicie sesión nuevamente."
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Token expirado")
                .errors(error)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler error generico de JWT
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
        JwtException ex,
        HttpServletRequest request
    ) {
        Map<String, String> error = Map.of(
            "error",
            "El token proporcionado es inválido o está corrupto."
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Error de token")
                .errors(error)
                .endpoint(request.getRequestURI())
                .build()
        );
    }
}
