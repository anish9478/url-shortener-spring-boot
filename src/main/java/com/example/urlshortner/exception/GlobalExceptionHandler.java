package com.example.urlshortner.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleShortCodeNotFound(ShortCodeNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("status", String.valueOf(404));
        error.put("error", "Not Found");
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseException(ResponseStatusException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("status ", ex.getStatusCode().value());
        map.put("status", ex.getStatusCode().toString());
        map.put("message", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(map);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 409);
        response.put("error", "Conflict");
        response.put("message", "A database constraint is violated");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}
