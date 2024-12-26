package org.restaurant.exceptions.handler;

import org.restaurant.exceptions.InvalidIdempotencyKeyException;
import org.restaurant.exceptions.ObjectNotValidException;
import org.restaurant.exceptions.OrderNotFoundException;
import org.restaurant.exceptions.UserNotValidException;
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

    @ExceptionHandler(UserNotValidException.class)
    public ResponseEntity<?> handleUserNotValidExceptiom(UserNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleUserNotValidExceptiom(OrderNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
