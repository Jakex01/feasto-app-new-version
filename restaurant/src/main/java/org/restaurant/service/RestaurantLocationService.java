package org.restaurant.service;

import org.springframework.http.ResponseEntity;

public interface RestaurantLocationService {
    ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId);
}
