package org.restaurant.service;

import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface LocationService {
     ResponseEntity<LocationResponse> createLocation(LocationRequest request, Authentication authentication);

     ResponseEntity<?> updateLocation(Long id, Authentication principal);

    ResponseEntity<String> getCurrentLocation(Authentication authentication);

    ResponseEntity<List<LocationNamesResponse>> getAllUsersLocations(Authentication authentication);
}
