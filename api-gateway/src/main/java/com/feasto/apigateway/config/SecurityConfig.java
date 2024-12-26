package com.feasto.apigateway.config;

import com.feasto.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    private static final String[] ADMIN_ROLES = {"ADMIN"};
    private static final String[] MANAGER_ROLES = {"MANAGER", "ADMIN"};
    private static final String[] USER_ROLES = {"USER", "MANAGER", "ADMIN"};
    private static final String API_AUTH_URL = "/api/security/**";
    private static final String API_RESTAURANT_URL = "/api/restaurant";
    private static final String API_ORDER_URL = "/api/order";
    private static final String API_RESTAURANT_LOCATION_URL = "/api/restaurant-location/**";
    private static final String API_RATING_URL = "/api/rating/**";
    private static final String API_MENU_ITEM_URL = "/api/restaurant/menu-item/**";
    private static final String API_USER_FAVOURITE = "/api/restaurant-like/**";
    private static final String API_BASKET_URL_ = "/api/basket/**";
    private static final String API_CHAT_URL_ = "/api/chat/**";
    private static final String API_STATS_URL_ = "/api/stats/**";
    private static final String API_CHAT_WS = "/ws/chat/**";
    private static final String API_PAYMENT = "/api/payment/**";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(API_AUTH_URL).permitAll()
                        .pathMatchers(API_PAYMENT).hasAnyRole(USER_ROLES)
                        .pathMatchers(API_CHAT_WS).permitAll()
                        .pathMatchers(API_ORDER_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.GET, API_RESTAURANT_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(API_BASKET_URL_).hasAnyRole(USER_ROLES)
                        .pathMatchers(API_CHAT_URL_).hasAnyRole(USER_ROLES)
                        .pathMatchers(API_STATS_URL_).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.POST, API_RESTAURANT_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.PATCH, API_RESTAURANT_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.DELETE, API_RESTAURANT_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.GET, API_RESTAURANT_LOCATION_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.POST, API_RESTAURANT_LOCATION_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.PATCH, API_RESTAURANT_LOCATION_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.DELETE, API_RESTAURANT_LOCATION_URL).hasAnyRole(MANAGER_ROLES)
                        .pathMatchers(HttpMethod.GET, API_RATING_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.POST, API_RATING_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.GET, API_MENU_ITEM_URL).hasAnyRole(USER_ROLES)
                        .pathMatchers(HttpMethod.PATCH, API_MENU_ITEM_URL).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.DELETE, API_MENU_ITEM_URL).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(API_USER_FAVOURITE).hasAnyRole(USER_ROLES)
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
    @Bean
    @Primary
    WebSocketClient tomcatWebSocketClient() {
        return new TomcatWebSocketClient();
    }
    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        return new TomcatRequestUpgradeStrategy();
    }

    private AuthenticationFilter jwtValidationFilter() {
        return new AuthenticationFilter(secretKey);
    }
}

