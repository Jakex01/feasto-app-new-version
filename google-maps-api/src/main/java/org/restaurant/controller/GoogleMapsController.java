package org.restaurant.controller;

import com.google.maps.model.TravelMode;
import lombok.RequiredArgsConstructor;
import org.restaurant.service.GoogleMapsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-maps")
public class GoogleMapsController {
    private final GoogleMapsService googleMapsService;

    @GetMapping("/calculate-travel-time")
    public ResponseEntity<String> getTravelTime(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(defaultValue = "DRIVING") TravelMode travelMode) {
        try {
            String result = googleMapsService.calculateTravelTime(origin, destination, travelMode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating travel time: " + e.getMessage());
        }
    }
}
