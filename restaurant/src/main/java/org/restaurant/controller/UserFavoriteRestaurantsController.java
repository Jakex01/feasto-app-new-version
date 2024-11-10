package org.restaurant.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.service.UserFavoriteRestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/restaurant-like")
public class UserFavoriteRestaurantsController {

    private final UserFavoriteRestaurantService userFavoriteRestaurantService;

    @PostMapping()
    public ResponseEntity<?> addFavoriteRestaurant(@RequestParam @NonNull Long restaurantId){
    return userFavoriteRestaurantService.addFavoriteRestaurant(restaurantId);
    }
    @DeleteMapping()
    public ResponseEntity<?> deleteFavoriteRestaurant(@RequestParam @NonNull Long restaurantId){
        return userFavoriteRestaurantService.deleteFavoriteRestaurant(restaurantId);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="security", fallbackMethod = "fallBackFavorite")
    @TimeLimiter(name="security")
    public CompletableFuture<?> getFavourites(){
      return   CompletableFuture.supplyAsync(userFavoriteRestaurantService::getFavourites);
    }

}
