package org.restaurant.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.UserNotEligibleException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.model.RatingEntity;
import org.restaurant.repository.RatingRepository;
import org.restaurant.request.PostRatingRequest;
import org.restaurant.response.RatingResponse;
import org.restaurant.response.ReviewResponse;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.restaurant.validators.LocationValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UrlConfig urlConfig;
    private final LocationValidator locationValidator;
    private final UserDetailsClient userDetailsClient;
    @Transactional
    public ResponseEntity<?> postRating(PostRatingRequest postRatingRequest, String token) {
        locationValidator.validateRequest(postRatingRequest);
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }
        boolean userValidity = Boolean.TRUE.equals(userDetailsClient
                .checkUsersEligibility(urlConfig
                        .getOrderService(), userEmail,
                        postRatingRequest.restaurantId(),
                        boolean.class,
                        JwtUtil.extractToken(token)).block());
        if (!userValidity) {
            throw new UserNotEligibleException("User is not eligible to rate this restaurant as no order has been placed.");
        }
        RatingEntity rating = MapStructMapper.INSTANCE.requestRatingToRatingEntity(postRatingRequest);
        rating.setUserEmail(userEmail);
        ratingRepository.save(rating);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    public ResponseEntity<List<RatingResponse>> getRatingByRestaurantId(Long restaurantId) {
       List<RatingResponse> ratingEntityList =  ratingRepository
                .findAllByRestaurantRestaurantId(restaurantId)
               .stream()
               .map(MapStructMapper.INSTANCE::ratingEntityToRatingResponse)
               .toList();

       return ResponseEntity.ok(ratingEntityList);
    }

    public ResponseEntity<RatingResponse> getAverageRatingByRestaurantId(Long restaurantId, Double averageRating) {
        RatingResponse ratingResponse = ratingRepository
                .findAllByRestaurantRestaurantId(restaurantId)
                .stream()
                .min(Comparator.comparingDouble(rating ->
                        Math.abs(rating.getRating() - averageRating)))
                .map(MapStructMapper.INSTANCE::ratingEntityToRatingResponse)
                .orElse(null);

        return ResponseEntity.ok(ratingResponse);
    }

    public ResponseEntity<List<ReviewResponse>> getAllRatingsByUser(String token) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }
       List<RatingEntity> ratingEntityList = ratingRepository.findAllByUserEmail(userEmail);

       List<ReviewResponse> reviewResponses = ratingEntityList
                .stream()
                .map(rating -> new ReviewResponse(
                rating.getRestaurant().getName(),
                rating.getRating(),
                rating.getReview(),
                rating.getCreateDate()
        ))
        .toList();
       return ResponseEntity.ok(reviewResponses);
    }
}
