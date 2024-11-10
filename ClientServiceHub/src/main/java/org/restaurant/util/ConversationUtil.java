package org.restaurant.util;

import lombok.RequiredArgsConstructor;
import org.restaurant.response.RestaurantConversationsResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationUtil {
    private final WebClient webClient;
    private final WebClient securityWebClient;

    public ConversationUtil(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://restaurant-service/api/restaurant").build();
        this.securityWebClient = webClientBuilder.baseUrl("http://security-service/api/auth").build();
    }

    public Long getUsersId(String token) {
        return securityWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/decode-token")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public Set<RestaurantConversationsResponse> getRestaurantNames(Set<Long> restaurantIds) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/details")
                        .queryParam("ids", String.join(",", restaurantIds.stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList())))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<RestaurantConversationsResponse>>() {})
                .block();
    }
}
