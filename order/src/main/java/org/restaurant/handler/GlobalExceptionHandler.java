package org.restaurant.handler;

import org.restaurant.exceptions.ObjectNotValidException;
import org.restaurant.exceptions.OrderNotFoundException;
import org.restaurant.exceptions.UserNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleException(ObjectNotValidException exp) {
        return ResponseEntity
                .badRequest()
                .body(exp.getErrorMessages());

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
