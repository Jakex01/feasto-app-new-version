package com.feasto.apigateway.filter;

import com.feasto.apigateway.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component

public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    private final RouteValidator routeValidator;
    private final RestTemplate template;

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator routeValidator, RestTemplate template, JwtUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.template = template;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return (((exchange, chain) -> {
            if(routeValidator.isSecured.test(exchange.getRequest())){
                // header contains token or not
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if(authHeader!=null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }
                try{
                    jwtUtil.validateToken(authHeader);
                }catch (Exception e){
                    throw new RuntimeException("unauthorized access to application!");
                }
            }
        return chain.filter(exchange);

        }));
    }
    public static class Config{

    }

}
