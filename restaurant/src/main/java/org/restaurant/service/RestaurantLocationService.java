package org.restaurant.service;

import org.restaurant.request.PostLocationRequest;
import org.springframework.http.ResponseEntity;

public interface RestaurantLocationService {
    ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId);

    ResponseEntity<Void> postRestaurantLocation(PostLocationRequest postLocationRequest, Long restaurantId);

    ResponseEntity<Void> deleteRestaurantLocationById(Long locationId);
}
