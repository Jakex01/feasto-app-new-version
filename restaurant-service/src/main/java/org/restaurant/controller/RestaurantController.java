package org.restaurant.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.FilterRestaurant;
import org.restaurant.request.update.UpdateRestaurantRequest;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.response.RestaurantsOwnerResponse;
import org.restaurant.service.ElasticRestaurantService;
import org.restaurant.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant/manage")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ElasticRestaurantService elasticRestaurantService;

    @PostMapping
    public ResponseEntity<?> addRestaurant(@RequestBody CreateRestaurantRequestDuplicate createRestaurant,
                                           @RequestHeader(value = "Authorization") String token){
       return restaurantService.createRestaurant(createRestaurant, token);
    }
    @PatchMapping("/{restaurantId}")
    public ResponseEntity<Void> updateRestaurantById(@PathVariable @NotNull Long restaurantId,
                                                     @RequestBody UpdateRestaurantRequest updateRestaurantRequest,
                                                     @RequestHeader(value = "Authorization") String token) {
        return restaurantService.updateRestaurantById(restaurantId, updateRestaurantRequest, token);
    }
    @PostMapping("/photo/{restaurantId}")
    public ResponseEntity<Void> updateRestaurantPhoto(@RequestParam("image") MultipartFile file,
                                                      @PathVariable("restaurantId") Long restaurantId) throws IOException {
        return restaurantService.updateRestaurantPhoto(file, restaurantId);
    }
    @GetMapping
    public ResponseEntity<Page<ElasticRestaurant>> getAllRestaurants(@RequestParam(defaultValue = "0") @NotNull int page,
                                                                    @RequestParam(defaultValue = "10") @NotNull int size){
        return elasticRestaurantService.getAllRestaurants(page, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<RestaurantsOwnerResponse>> getAllRestaurantsOwner(@RequestHeader(value = "Authorization") String token) {
        return restaurantService.getAllRestaurantsOwner(token);
    }
    @PostMapping("/search")
    public ResponseEntity<Page<ElasticRestaurant>> search(
            @RequestBody FilterRestaurant filterRestaurant,
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(defaultValue = "0") @NotNull int page,
            @RequestParam(defaultValue = "10") @NotNull int size) throws RestaurantSearchException {
       Page<ElasticRestaurant> elasticRestaurants =  elasticRestaurantService.filterRestaurants(filterRestaurant, token, PageRequest.of(page, size));
        return ResponseEntity.ok(elasticRestaurants);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable("id") @Valid Long restaurantId, @RequestHeader(value = "Authorization") String token){
        return restaurantService.findRestaurantById(restaurantId, token);
    }
    @GetMapping("/details")
    public ResponseEntity<Set<RestaurantConversationResponse>> getRestaurantsByIds(@RequestParam("ids") Set<Long> ids) {
        Set<RestaurantConversationResponse> responses = restaurantService.findRestaurantsByIds(ids);
        return ResponseEntity.ok(responses);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantById(@PathVariable("id") @Valid Long restaurantId) {
        restaurantService.deleteRestaurantById(restaurantId);
     return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/check-managers")
    public ResponseEntity<Map<Long, Boolean>> checkManagers(@RequestBody Set<Long> restaurantIds, @RequestHeader(value = "Authorization") String token) {
       return restaurantService.checkManagers(restaurantIds, token);
    }
}
