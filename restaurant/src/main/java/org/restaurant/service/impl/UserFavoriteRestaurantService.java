package org.restaurant.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.model.FavoriteRestaurantEntity;
import org.restaurant.repository.UserFavoriteRestaurantRepository;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserFavoriteRestaurantService {

    private final UserFavoriteRestaurantRepository userFavoriteRestaurantRepository;
    private final UserDetailsClient userDetailsClient;
    private final UrlConfig urlConfig;
    @CircuitBreaker(name="security", fallbackMethod = "fallBackFavoriteAdd")
    @TimeLimiter(name="security")
    @Transactional
    public ResponseEntity<?> addFavoriteRestaurant(Long restaurantId, String token)
    {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        FavoriteRestaurantEntity favoriteRestaurantEntity = FavoriteRestaurantEntity
                .builder()
                .restaurantId(restaurantId)
                .userEmail(userEmail)
                .build();

        if (userEmail!=null) {
            userFavoriteRestaurantRepository.saveAndFlush(favoriteRestaurantEntity);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            throw new UserNotFoundException("UserEmail can't be blank");
        }
    }
    @Transactional
    public ResponseEntity<?> deleteFavoriteRestaurant(Long restaurantId, String token) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        userFavoriteRestaurantRepository.findByRestaurantIdAndUserEmail(restaurantId, userEmail)
                .ifPresent(userFavoriteRestaurantRepository::delete);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    public CompletableFuture<List<Long>> getFavourites(String token) {
        return CompletableFuture.supplyAsync(() -> {
            String userEmail = userDetailsClient
                    .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

            if (userEmail != null) {
                return userFavoriteRestaurantRepository
                        .findAllByUserEmail(userEmail)
                        .stream()
                        .map(FavoriteRestaurantEntity::getRestaurantId)
                        .toList();
            } else {
                throw new UserNotFoundException("userEmail can't be blank");
            }
        });
    }

}
