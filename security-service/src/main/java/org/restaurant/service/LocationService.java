package org.restaurant.service;

import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface LocationService {
     ResponseEntity<LocationResponse> createLocation(LocationRequest request, String token);

     ResponseEntity<?> updateLocation(Long id, String token);

    ResponseEntity<String> getCurrentLocation(String token);

    ResponseEntity<List<LocationResponse>> getAllUsersLocations(String token);

    ResponseEntity<List<LocationNamesResponse>> getAllUsersShortenLocationsList(String token);
}
