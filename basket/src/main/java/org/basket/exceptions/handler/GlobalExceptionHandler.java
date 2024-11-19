package org.basket.exceptions.handler;


import org.basket.exceptions.BadCredentialException;
import org.basket.exceptions.ObjectNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleObjectNotValid(ObjectNotValidException exception) {
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorMessages());
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleBadCredentialException(BadCredentialException exception) {
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

}
