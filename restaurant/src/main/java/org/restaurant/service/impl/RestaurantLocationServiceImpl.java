package org.restaurant.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.LocationNotFoundException;
import org.restaurant.mapstruct.dto.LocationMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.request.PostLocationRequest;
import org.restaurant.service.RestaurantLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantLocationServiceImpl implements RestaurantLocationService {

    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    @Override
    public ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId) {

        LocationEntity locationEntity = locationRepository.findAllByRestaurantRestaurantIdAndCity(restaurantId, city)
                .stream()
                .findFirst()
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
        String location = locationEntity.getStreet() + " " + locationEntity.getStreetNumber() + " " + locationEntity.getCity();
        return ResponseEntity.ok(location);
    }

    @Override
    public ResponseEntity<Void> postRestaurantLocation(PostLocationRequest postLocationRequest, Long restaurantId) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));
        LocationEntity locationEntity = LocationMapper.INSTANCE.locationRequestToLocationEntity(postLocationRequest);
        locationEntity.setRestaurant(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantLocationById(Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));
        RestaurantEntity restaurant = locationEntity.getRestaurant();
        if (restaurant != null) {
            restaurant.getLocations().remove(locationEntity);
        }
        return ResponseEntity.noContent().build();
    }
}
