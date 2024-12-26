package org.restaurant.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.restaurant.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/location")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @RequestBody LocationRequest locationRequest,
            @RequestHeader("Authorization") String token
    ) {
        return locationService.createLocation(locationRequest, token);
    }
    @PatchMapping
    public ResponseEntity<?> updateLocation(
            @RequestParam @NonNull Long id,
            @RequestHeader("Authorization") String token
    ){
        return locationService.updateLocation(id, token);
    }
    @GetMapping
    public ResponseEntity<String> getCurrentLocation(@RequestHeader("Authorization") String token){
        return locationService.getCurrentLocation(token);
    }
    @GetMapping("/personal-locations")
    public ResponseEntity<List<LocationResponse>> getAllUsersLocations(@RequestHeader("Authorization") String token) {
        return locationService.getAllUsersLocations(token);
    }
    @GetMapping("/shorten-list")
    public ResponseEntity<List<LocationNamesResponse>> getAllUsersShortenLocationsList(@RequestHeader("Authorization") String token) {
        return locationService.getAllUsersShortenLocationsList(token);
    }
}
