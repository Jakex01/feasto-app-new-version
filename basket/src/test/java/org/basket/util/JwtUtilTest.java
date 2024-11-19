package org.basket.util;

import org.basket.exceptions.BadCredentialException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void extractToken_ShouldReturnToken_WhenValidBearerTokenProvided() {
        String bearerToken = "Bearer validToken123";
        String extractedToken = JwtUtil.extractToken(bearerToken);
        assertEquals("validToken123", extractedToken);
    }

    @Test
    void extractToken_ShouldThrowException_WhenTokenIsNull() {
        Exception exception = assertThrows(BadCredentialException.class, () ->
                JwtUtil.extractToken(null)
        );
        assertEquals("Invalid token: Token cannot be null", exception.getMessage());
    }

    @Test
    void extractToken_ShouldThrowException_WhenTokenDoesNotStartWithBearer() {
        String invalidToken = "InvalidToken123";
        Exception exception = assertThrows(BadCredentialException.class, () ->
                JwtUtil.extractToken(invalidToken)
        );
        assertEquals("Invalid token: Token does not start with 'Bearer '", exception.getMessage());
    }
}