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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/restaurant/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/restaurant/**").hasAnyRole("MANAGER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/restaurant/**").hasRole("MANAGER")
                        .pathMatchers("/api/statistics/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private AuthenticationFilter jwtValidationFilter() {
        return new AuthenticationFilter(secretKey);
    }

}

