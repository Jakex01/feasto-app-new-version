package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.LocationNotFoundException;
import org.restaurant.model.LocationEntity;
import org.restaurant.repository.LocationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantLocationServiceImpl implements RestaurantLocationService{

    private final LocationRepository locationRepository;
    @Override
    public ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId) {

        LocationEntity locationEntity = locationRepository.findAllByRestaurantRestaurantIdAndCity(restaurantId, city)
                .stream()
                .findFirst()
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
        String location = locationEntity.getStreet() + " " + locationEntity.getStreetNumber() + " " + locationEntity.getCity();
        return ResponseEntity.ok(location);
    }
}
