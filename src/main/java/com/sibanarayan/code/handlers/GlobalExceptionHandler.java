package com.sibanarayan.code.handlers;


import com.sibanarayan.code.exceptions.EntityAlreadyExistException;
import com.sibanarayan.code.exceptions.ResourceNotFoundException;
import com.sibanarayan.code.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<String> handleUserExists(EntityAlreadyExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ── 404 Not Found ────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 400 Bad Request ──────────────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── 500 Fallback ─────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> build(HttpStatus status,
                                                      String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return ResponseEntity.status(status).body(body);
    }
}