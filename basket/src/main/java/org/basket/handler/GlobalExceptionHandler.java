package org.basket.handler;

import org.basket.exception.BadCredentialException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<?> handleException(BadCredentialException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getMessage());

    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleException(ResponseStatusException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getMessage());

    }
}
