package org.restaurant.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.restaurant.service.impl.UserFavoriteRestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurant-like")
public class UserFavoriteRestaurantsController {

    private final UserFavoriteRestaurantService userFavoriteRestaurantService;

    @PostMapping()
    public ResponseEntity<?> addFavoriteRestaurant(@RequestParam @NonNull Long restaurantId, @RequestHeader String token){
    return userFavoriteRestaurantService.addFavoriteRestaurant(restaurantId, token);
    }
    @DeleteMapping()
    public ResponseEntity<?> deleteFavoriteRestaurant(@RequestParam @NonNull Long restaurantId, @RequestHeader String token){
        return userFavoriteRestaurantService.deleteFavoriteRestaurant(restaurantId, token);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="security", fallbackMethod = "fallBackFavorite")
    @TimeLimiter(name="security")
    public CompletableFuture<?> getFavourites(@RequestHeader String token){
        return CompletableFuture.supplyAsync(() -> userFavoriteRestaurantService.getFavourites(token));
    }

}
