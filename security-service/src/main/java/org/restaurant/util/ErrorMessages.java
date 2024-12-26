package org.restaurant.util;

public final class ErrorMessages {
    public static final String MISSING_TOKEN = "Authorization token is missing or empty";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_TOKEN = "Invalid token: cannot retrieve user email";

    private ErrorMessages() {
        // Prywatny konstruktor, żeby zapobiec tworzeniu instancji
    }
}

