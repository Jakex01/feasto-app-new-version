package org.restaurant.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.FilterRestaurant;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.service.ElasticRestaurantService;
import org.restaurant.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantServiceImpl;
    private final ElasticRestaurantService elasticRestaurantService;

    @PostMapping
    public ResponseEntity<?> addRestaurant(@RequestBody CreateRestaurantRequestDuplicate createRestaurant,
                                           @RequestHeader(value = "Authorization") String token){
       return restaurantServiceImpl.createRestaurant(createRestaurant, token);
    }
    @GetMapping
    public ResponseEntity<Page<RestaurantEntity>> getAllRestaurants(@RequestParam(defaultValue = "0") @NotNull int page,
                                                                    @RequestParam(defaultValue = "10") @NotNull int size){
        return restaurantServiceImpl.getAllRestaurants(page, size);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<ElasticRestaurant>> search(
            @RequestBody FilterRestaurant filterRestaurant,
            @RequestParam(defaultValue = "0") @NotNull int page,
            @RequestParam(defaultValue = "10") @NotNull int size) throws RestaurantSearchException {
       Page<ElasticRestaurant> elasticRestaurants =  elasticRestaurantService.filterRestaurants(filterRestaurant, PageRequest.of(page, size));
        return ResponseEntity.ok(elasticRestaurants);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable("id") @Valid Long restaurantId){
        return restaurantServiceImpl.findRestaurantById(restaurantId);
    }
    @GetMapping("/details")
    public ResponseEntity<Set<RestaurantConversationResponse>> getRestaurantsByIds(@RequestParam("ids") Set<Long> ids) {
        Set<RestaurantConversationResponse> responses = restaurantServiceImpl.findRestaurantsByIds(ids);
        return ResponseEntity.ok(responses);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantById(@PathVariable("id") @Valid Long restaurantId) {
     restaurantServiceImpl.deleteRestaurantById(restaurantId);
     return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
