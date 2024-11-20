package org.example.util;

import lombok.RequiredArgsConstructor;
import org.example.model.dto.CustomerDetailsDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CustomerDetailsService {

    private final WebClient webClient;

    public CustomerDetailsDto getCustomerDetails(Long userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToMono(CustomerDetailsDto.class)
                .block();
    }

}
