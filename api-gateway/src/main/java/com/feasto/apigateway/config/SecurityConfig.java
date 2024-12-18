package com.feasto.apigateway.config;

import com.feasto.apigateway.filter.AuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    private static final String[] ADMIN_ROLES = {"ADMIN"};
    private static final String[] MANAGER_ROLES = {"MANAGER", "ADMIN"};
    private static final String[] USER_ROLES = {"USER", "MANAGER", "ADMIN"};
    private static final String API_AUTH_URL = "/api/auth/**";
    private static final String API_RESTAURANT_URL = "/api/restaurant";
    private static final String API_RESTAURANT_LOCATION_URL = "/api/restaurant-location/**";
    private static final String API_RATING_URL = "/api/rating/**";
    private static final String API_MENU_ITEM_URL = "/api/restaurant/menu-item/**";
    private static final String API_USER_FAVOURITE = "/api/restaurant-like/**";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(API_AUTH_URL).permitAll()
                        .pathMatchers(HttpMethod.GET, API_RESTAURANT_URL).hasAnyRole(USER_ROLES)
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

    private AuthenticationFilter jwtValidationFilter() {
        return new AuthenticationFilter(secretKey);
    }
}

