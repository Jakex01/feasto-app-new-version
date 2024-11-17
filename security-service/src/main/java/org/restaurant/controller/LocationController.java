package org.restaurant.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.restaurant.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/location")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @RequestBody LocationRequest locationRequest,
            Authentication authentication
    ) {
        return locationService.createLocation(locationRequest, authentication);
    }
    @PatchMapping
    public ResponseEntity<?> updateLocation(
            @RequestParam @NonNull Long id,
            Authentication principal
    ){
        return locationService.updateLocation(id, principal);
    }
    @GetMapping
    public ResponseEntity<String> getCurrentLocation(Authentication authentication){
        return locationService.getCurrentLocation(authentication);
    }
    @GetMapping("/personal-locations")
    public ResponseEntity<List<LocationNamesResponse>> getAllUsersLocations(Authentication authentication) {
        return locationService.getAllUsersLocations(authentication);
    }
}
