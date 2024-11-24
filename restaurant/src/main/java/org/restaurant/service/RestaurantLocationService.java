package org.restaurant.service;

import org.restaurant.request.PostLocationRequest;
import org.restaurant.request.update.UpdateLocationRequest;
import org.springframework.http.ResponseEntity;

public interface RestaurantLocationService {
    ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId);

    ResponseEntity<Void> postRestaurantLocation(PostLocationRequest postLocationRequest, Long restaurantId, String token);

    ResponseEntity<Void> deleteRestaurantLocationById(Long locationId);

    ResponseEntity<Void> updateRestaurantLocationById(UpdateLocationRequest request, Long locationId, String token);
}
