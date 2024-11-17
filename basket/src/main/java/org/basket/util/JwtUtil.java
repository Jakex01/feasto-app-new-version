package org.basket.util;

import org.basket.exception.BadCredentialException;

public class JwtUtil {
    public static String extractToken(String bearerToken) {
        if (bearerToken == null) {
            throw new BadCredentialException("Invalid token: Token cannot be null");
        }
        if (!bearerToken.startsWith("Bearer ")) {
            throw new BadCredentialException("Invalid token: Token does not start with 'Bearer '");
        }
        return bearerToken.substring(7);
    }
}

