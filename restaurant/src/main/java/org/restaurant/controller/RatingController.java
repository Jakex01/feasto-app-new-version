package org.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restaurant.request.PostRatingRequest;
import org.restaurant.response.ReviewResponse;
import org.restaurant.service.impl.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> postRating(@RequestBody PostRatingRequest postRatingRequest,
                                        @RequestHeader(value = "Authorization") String token){
        return ratingService.postRating(postRatingRequest, token);
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
    @GetMapping("/user}")
    public ResponseEntity<List<ReviewResponse>> getAllRatingsByUser(@RequestHeader(value = "Authorization") String token) {
        return ratingService.getAllRatingsByUser(token);
    }
}
