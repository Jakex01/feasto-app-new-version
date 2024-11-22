package org.example.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsClient {

    private final WebClient webClient;

    public <T> Mono<T> fetchData(String uri, Class<T> responseType, String jwtToken, Long userId) {
        return webClient.get()
                .uri(uri, userId)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class, ex ->
                        log.error("Error fetching data: "
                                + ex.getStatusCode()
                                + " - " + ex.getResponseBodyAsString()));
    }

}
