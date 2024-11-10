package org.restaurant.handler;

import org.restaurant.exceptions.LocationNotFoundException;
import org.restaurant.exceptions.ObjectNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleException(ObjectNotValidException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getErrorMessages());

    }
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<?> handleLocationNotFound(ObjectNotValidException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getErrorMessages());

    }
}
