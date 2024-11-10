package org.restaurant.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.model.FavoriteRestaurantEntity;
import org.restaurant.repository.UserFavoriteRestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
public class UserFavoriteRestaurantService {

    private final UserFavoriteRestaurantRepository userFavoriteRestaurantRepository;
    private final WebClient webClient;

    @CircuitBreaker(name="security", fallbackMethod = "fallBackFavoriteAdd")
    @TimeLimiter(name="security")
    public ResponseEntity<?> addFavoriteRestaurant(Long restaurantId)
    {
        Long userId = webClient.get()
                .uri("http://localhost:8083/api/auth/user")
                .retrieve()
                .bodyToMono(Long.class)
                .block();
        FavoriteRestaurantEntity favoriteRestaurantEntity = FavoriteRestaurantEntity
                .builder()
                .restaurantId(restaurantId)
                .userId(userId)
                .build();

        if(userId!=null){
            userFavoriteRestaurantRepository.saveAndFlush(favoriteRestaurantEntity);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }else {
            throw new IllegalArgumentException("UserId can't be null");
        }
    }
    public ResponseEntity<?> deleteFavoriteRestaurant(Long restaurantId) {
        userFavoriteRestaurantRepository.findById(restaurantId)
                .ifPresent(userFavoriteRestaurantRepository::delete);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @SneakyThrows
    public CompletableFuture<List<Long>> getFavourites() {
        return CompletableFuture.supplyAsync(() -> {
            Long userId = webClient.get()
                    .uri("http://localhost:8083/api/auth/user")
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();

            if (userId != null) {
                return userFavoriteRestaurantRepository
                        .findAllByUserId(userId)
                        .stream()
                        .map(FavoriteRestaurantEntity::getRestaurantId)
                        .toList();
            } else {
                throw new IllegalArgumentException("UserId can't be null");
            }
        });
    }

}
