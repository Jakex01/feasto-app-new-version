package org.restaurant.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsClient {

    private final WebClient webClient;

    public <T> Mono<T> fetchData(String uri, Class<T> responseType, String jwtToken) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class, ex ->
                        log.error("Error fetching data: "
                                + ex.getStatusCode()
                                + " - " + ex.getResponseBodyAsString()));
    }
    public <T> Mono<T> checkUsersEligibility(String uri, String userEmail, Long restaurantId, Class<T> responseType, String jwtToken) {
        var payload = Map.of(
                "userEmail", userEmail,
                "restaurantId", restaurantId
        );

        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + jwtToken)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class, ex ->
                        log.error("Error posting data: "
                                + ex.getStatusCode()
                                + " - " + ex.getResponseBodyAsString()));
    }

}
