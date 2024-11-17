package com.feasto.apigateway.filter;

import com.feasto.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator routeValidator;
    private final JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_PREFIX_LENGTH = 7;

    private static final String MANAGER = "MANAGER";
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    public AuthenticationFilter(RouteValidator routeValidator, JwtUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getPath().toString();
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (requiresRoleCheck(requestPath)) {
                    return handleAuthorization(exchange, chain, requestPath);
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleAuthorization(ServerWebExchange exchange, GatewayFilterChain chain, String requestPath) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            String token = authHeader.substring(TOKEN_PREFIX_LENGTH);
            try {
                List<String> roles = jwtUtil.extractRoles(token);
                List<String> requiredRoles = PathRoles.getRolesForPath(requestPath);
                if (requiredRoles == null || roles.stream().noneMatch(requiredRoles::contains)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private boolean requiresRoleCheck(String path) {
        List<String> roles = PathRoles.getRolesForPath(path);
        return roles != null && !roles.isEmpty();
    }

    public enum PathRoles {
        RESTAURANT("/api/restaurant", List.of(ADMIN, MANAGER)),
        RESTAURANT_SEARCH("/api/restaurant/search", List.of(ADMIN, MANAGER, USER)),
        RESTAURANT_ALL("/api/restaurant", List.of(ADMIN, MANAGER, USER)),
        RESTAURANT_BY_ID("/api/restaurant/{id}", List.of(ADMIN, MANAGER, USER)),
        RESTAURANT_DETAILS("/api/restaurant/details", List.of(ADMIN, MANAGER, USER));

        private final String path;
        private final List<String> roles;

        PathRoles(String path, List<String> roles) {
            this.path = path;
            this.roles = roles;
        }

        public static List<String> getRolesForPath(String requestPath) {
            return Arrays.stream(values())
                    .filter(pathRole -> requestPath.matches(pathRole.path.replace("{id}", "\\d+")))
                    .findFirst()
                    .map(pathRole -> pathRole.roles)
                    .orElse(null);
        }
    }

    public static class Config {
    }
}
