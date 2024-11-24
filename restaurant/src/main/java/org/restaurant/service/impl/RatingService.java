package org.restaurant.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.repository.RatingRepository;
import org.restaurant.request.PostRatingRequest;
import org.restaurant.response.RatingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    @Transactional
    public ResponseEntity<?> postRating(PostRatingRequest postRatingRequest) {
        ratingRepository.save(MapStructMapper.INSTANCE.requestRatingToRatingEntity(postRatingRequest));
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
}
