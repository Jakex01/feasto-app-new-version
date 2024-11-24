package org.restaurant.handler;

import org.restaurant.exceptions.*;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> handleLocationNotFound(LocationNotFoundException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getErrorMessages());

    }

    @ExceptionHandler(RestaurantSearchException.class)
    public ResponseEntity<?> handleRestaurantSearch(RestaurantSearchException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getMessage());

    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException exp){
        return ResponseEntity
                .notFound()
                .build();

    }
    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<?> handleMenuItemNotFound(MenuItemNotFoundException exp){
        return ResponseEntity
                .badRequest()
                .body(exp.getMessage());

    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleMenuItemNotFound(AccessDeniedException exp){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exp.getMessage());

    }
    @ExceptionHandler(UserNotEligibleException.class)
    public ResponseEntity<?> handleUserNotEligibleException(UserNotEligibleException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
