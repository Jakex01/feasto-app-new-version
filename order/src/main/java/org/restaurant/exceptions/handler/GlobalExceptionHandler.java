package org.restaurant.exceptions.handler;

import org.restaurant.exceptions.InvalidIdempotencyKeyException;
import org.restaurant.exceptions.ObjectNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidIdempotencyKeyException.class)
    public ResponseEntity<?> handleException(InvalidIdempotencyKeyException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleObjectNotValid(ObjectNotValidException exception) {
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getErrorMessages());
    }
}
