package org.basket.exceptions;

public class BadCredentialException extends RuntimeException {

    public BadCredentialException(String message) {
        super(message);
    }

    public BadCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}
