package org.restaurant.service;

import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.update.UpdateRestaurantRequest;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface RestaurantService {
    ResponseEntity<?> createRestaurant(CreateRestaurantRequestDuplicate createRestaurant, String token);
    ResponseEntity<Page<RestaurantEntity>> getAllRestaurants(int page, int size);
    void deleteRestaurantById(Long restaurantId);
    ResponseEntity<RestaurantResponse> findRestaurantById(Long restaurantId);
    Set<RestaurantConversationResponse> findRestaurantsByIds(Set<Long> ids);

    ResponseEntity<Void> updateRestaurantById(Long restaurantId, UpdateRestaurantRequest updateRestaurantRequest, String token);
}
