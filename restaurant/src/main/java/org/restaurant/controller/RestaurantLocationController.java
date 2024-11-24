package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.request.PostLocationRequest;
import org.restaurant.service.RestaurantLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant-location")
public class RestaurantLocationController {

    private final RestaurantLocationService restaurantLocationService;

    @GetMapping
    public ResponseEntity<String> getRestaurantLocationByCity(@RequestParam String city, @RequestParam Long restaurantId){
        return restaurantLocationService.getRestaurantLocationByCity(city, restaurantId);
    }
    @PostMapping("/{restaurantId}")
    public ResponseEntity<Void> postRestaurantLocation(@RequestBody PostLocationRequest postLocationRequest, @PathVariable Long restaurantId) {
        return restaurantLocationService.postRestaurantLocation(postLocationRequest, restaurantId);
    }
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteRestaurantLocationById(@PathVariable Long locationId) {
        return restaurantLocationService.deleteRestaurantLocationById(locationId);
    }


}
