package org.restaurant.configuration;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Pobierz token z zapytania
        String query = request.getURI().getQuery(); // np. "token=abc123"
        if (query != null && query.startsWith("token=")) {
            String token = query.substring("token=".length());
            // Zaimplementuj logikę weryfikacji tokena
            if (validateToken(token)) {
                // Dodaj token do atrybutów sesji
                attributes.put("token", token);
                return true;
            }
        }
        // Zwróć false, jeśli token jest nieważny
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Możesz dodać tutaj dodatkowe logowanie, jeśli potrzebne
    }

    private boolean validateToken(String token) {
        // Twoja logika weryfikacji tokena, np. dekodowanie JWT
        return token != null && !token.isEmpty(); // Prosta weryfikacja jako przykład
    }
}
