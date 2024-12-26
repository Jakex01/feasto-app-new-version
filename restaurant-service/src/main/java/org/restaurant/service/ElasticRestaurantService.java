package org.restaurant.service;

import jakarta.validation.constraints.NotNull;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.request.FilterRestaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface ElasticRestaurantService {

    void saveRestaurant(RestaurantEntity restaurantEntity);
    ResponseEntity<Page<ElasticRestaurant>> getAllRestaurants(@RequestParam(defaultValue = "0") @NotNull int page,
                                                                    @RequestParam(defaultValue = "10") @NotNull int size);
    Page<ElasticRestaurant> filterRestaurants(FilterRestaurant filterRestaurant, String token, Pageable pageable) throws RestaurantSearchException;

    void deleteById(String restaurantId);
    void updateImageUrl(String restaurantId, String imageUrl);
}
