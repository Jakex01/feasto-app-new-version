package org.restaurant.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.request.PostLocationRequest;
import org.restaurant.request.update.UpdateLocationRequest;
import org.restaurant.response.RestaurantLocationNameResponse;
import org.restaurant.service.RestaurantLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant/restaurant-location")
public class RestaurantLocationController {

    private final RestaurantLocationService restaurantLocationService;

    @GetMapping
    public ResponseEntity<String> getRestaurantLocationByCity(@RequestParam @NotBlank String city,
                                                              @RequestParam @NotNull Long restaurantId){
        return restaurantLocationService.getRestaurantLocationByCity(city, restaurantId);
    }
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<RestaurantLocationNameResponse>> getRestaurantLocations(@PathVariable Long restaurantId) {
        return restaurantLocationService.getRestaurantLocations(restaurantId);
    }
    @PatchMapping("/{locationId}")
    public ResponseEntity<Void> updateRestaurantLocationById(@RequestBody UpdateLocationRequest request,
                                                             @PathVariable @NotNull Long locationId,
                                                             @RequestHeader(value = "Authorization") String token) {
        return restaurantLocationService.updateRestaurantLocationById(request, locationId, token);
    }
    @PostMapping("/{restaurantId}")
    public ResponseEntity<Void> postRestaurantLocation(@RequestBody PostLocationRequest postLocationRequest,
                                                       @PathVariable @NotNull Long restaurantId,
                                                       @RequestHeader(value = "Authorization") String token) {
        return restaurantLocationService.postRestaurantLocation(postLocationRequest, restaurantId, token);
    }
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteRestaurantLocationById(@PathVariable @NotNull Long locationId) {
        return restaurantLocationService.deleteRestaurantLocationById(locationId);
    }

}
