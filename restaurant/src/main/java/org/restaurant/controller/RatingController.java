package org.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restaurant.request.PostRatingRequest;
import org.restaurant.service.impl.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> postRating(@RequestBody PostRatingRequest postRatingRequest){
        return ratingService.postRating(postRatingRequest);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRatingByRestaurantId(@PathVariable("id") Long restaurantId){
        return ratingService.getRatingByRestaurantId(restaurantId);
    }
    @GetMapping("/average/{id}")
    public ResponseEntity<?> getAverageRatingByRestaurantId(
            @PathVariable("id") @Valid Long restaurantId,
            @RequestParam(value = "averageRating", required = false) Double averageRating){
        return ratingService.getAverageRatingByRestaurantId(restaurantId, averageRating);
    }
}
