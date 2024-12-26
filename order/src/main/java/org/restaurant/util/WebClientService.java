package org.restaurant.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class WebClientService {
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
    public Mono<Map<Long, Boolean>> fetchIsManagerMap(String uri, Set<Long> restaurantIds, String jwtToken) {
        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + jwtToken)
                .bodyValue(restaurantIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, Boolean>>() {})
                .doOnError(WebClientResponseException.class, ex ->
                        log.error("Error fetching isManagerMap data: "
                                + ex.getStatusCode()
                                + " - " + ex.getResponseBodyAsString()));
    }
}
