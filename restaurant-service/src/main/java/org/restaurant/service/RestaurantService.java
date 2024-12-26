package org.restaurant.service;

import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.update.UpdateRestaurantRequest;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.response.RestaurantsOwnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RestaurantService {
    ResponseEntity<?> createRestaurant(CreateRestaurantRequestDuplicate createRestaurant, String token);
    ResponseEntity<Page<RestaurantEntity>> getAllRestaurants(int page, int size);
    void deleteRestaurantById(Long restaurantId);
    ResponseEntity<RestaurantResponse> findRestaurantById(Long restaurantId, String token);
    Set<RestaurantConversationResponse> findRestaurantsByIds(Set<Long> ids);

    ResponseEntity<Void> updateRestaurantById(Long restaurantId, UpdateRestaurantRequest updateRestaurantRequest, String token);

    ResponseEntity<Void> updateRestaurantPhoto(MultipartFile file, Long restaurantId) throws IOException;

    ResponseEntity<List<RestaurantsOwnerResponse>> getAllRestaurantsOwner(String token);

    ResponseEntity<Map<Long, Boolean>> checkManagers(Set<Long> restaurantIds, String token);
}
