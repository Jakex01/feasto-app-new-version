package org.restaurant.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.AccessDeniedException;
import org.restaurant.exceptions.LocationNotFoundException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.mapstruct.dto.LocationMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.request.PostLocationRequest;
import org.restaurant.request.update.UpdateLocationRequest;
import org.restaurant.service.RestaurantLocationService;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.restaurant.validators.LocationValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantLocationServiceImpl implements RestaurantLocationService {

    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final LocationValidator locationValidator;
    private final UserDetailsClient userDetailsClient;
    private final UrlConfig urlConfig;

    @Override
    public ResponseEntity<String> getRestaurantLocationByCity(String city, Long restaurantId) {
        LocationEntity locationEntity = locationRepository.findAllByRestaurantRestaurantIdAndCity(restaurantId, city)
                .stream()
                .findFirst()
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        String location = formatLocation(locationEntity);
        return ResponseEntity.ok(location);
    }

    @Override
    public ResponseEntity<Void> postRestaurantLocation(PostLocationRequest postLocationRequest, Long restaurantId, String token) {
        locationValidator.validateRequest(postLocationRequest);

        RestaurantEntity restaurantEntity = getRestaurantById(restaurantId);
        validateUserIsManager(token, restaurantEntity);

        LocationEntity locationEntity = LocationMapper.INSTANCE.locationRequestToLocationEntity(postLocationRequest);
        locationEntity.setRestaurant(restaurantEntity);
        locationRepository.save(locationEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantLocationById(Long locationId) {
        LocationEntity locationEntity = getLocationById(locationId);

        RestaurantEntity restaurant = locationEntity.getRestaurant();
        if (restaurant != null) {
            restaurant.getLocations().remove(locationEntity);
        }
        locationRepository.delete(locationEntity);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateRestaurantLocationById(UpdateLocationRequest request, Long locationId, String token) {
        locationValidator.validateRequest(request);

        LocationEntity locationEntity = getLocationById(locationId);
        RestaurantEntity restaurantEntity = locationEntity.getRestaurant();

        validateUserIsManager(token, restaurantEntity);

        LocationMapper.INSTANCE.updateRequestToEntity(request, locationEntity);
        locationRepository.save(locationEntity);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // ======================= METODY POMOCNICZE =======================

    private LocationEntity getLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
    }

    private RestaurantEntity getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));
    }

    private void validateUserIsManager(String token, RestaurantEntity restaurantEntity) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }

        if (restaurantEntity == null) {
            throw new EntityNotFoundException("Restaurant associated with this location is not found");
        }

        if (!restaurantEntity.getManagerEmails().contains(userEmail)) {
            throw new AccessDeniedException("User does not have permissions to modify this restaurant");
        }
    }

    private String formatLocation(LocationEntity locationEntity) {
        return locationEntity.getStreet() + " " + locationEntity.getStreetNumber() + " " + locationEntity.getCity();
    }
}
