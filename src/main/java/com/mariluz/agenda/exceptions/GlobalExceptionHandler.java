package com.mariluz.agenda.exceptions;

import com.mariluz.agenda.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    // Handler bloques horarios
    @ExceptionHandler(SlotsAlreadyGenerated.class)
    public ResponseEntity<ErrorResponse> handleSlotsAlreadyGenerated(
        SlotsAlreadyGenerated ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Los bloques horarios ya han sido generados")
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
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Debe ser administrador para realizar esta operacion")
                .errors(Map.of("error", ex.getMessage()))
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

        ex
            .getBindingResult()
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
        Map<String, String> errors = new HashMap<>();
        errors.put(
            "error",
            "Revise el formato de hora en los campos enviados. Formato correcto: HH:mm, Ej: 09:00"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Hora mal formateada")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }
}
