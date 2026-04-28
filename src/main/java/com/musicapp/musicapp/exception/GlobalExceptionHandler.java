package com.musicapp.musicapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles all RuntimeExceptions from services
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        if (message != null && (
                message.contains("already") ||
                        message.contains("duplicate") ||
                        message.contains("exists")
        )) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)   // 409
                    .body(Map.of("message", message));
        }

        // 404 for not found
        if (message != null && message.contains("not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)  // 404
                    .body(Map.of("message", message));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", message != null ? message : "Something went wrong"));
    }

    // Handles illegal arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}