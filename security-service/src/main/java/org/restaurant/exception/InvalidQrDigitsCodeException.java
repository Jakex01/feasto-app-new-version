package org.restaurant.exception;

public class InvalidQrDigitsCodeException extends RuntimeException{
    public InvalidQrDigitsCodeException(String message) {
         super(message);
    }
}
