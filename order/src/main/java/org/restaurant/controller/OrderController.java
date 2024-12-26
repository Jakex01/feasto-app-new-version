package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.request.PostOrderRequest;
import org.restaurant.response.CustomerResponse;
import org.restaurant.response.OrderResponse;
import org.restaurant.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/order",produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @SneakyThrows
    public ResponseEntity<?> postOrder(@RequestBody PostOrderRequest postOrderRequest,
                                       @RequestHeader(value = "Authorization") String token,
                                       @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
        return orderService.postOrder(postOrderRequest, token, idempotencyKey);
    }
    @PatchMapping
    public ResponseEntity<?> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest, @RequestHeader(value = "Authorization") String token){
        return orderService.updateOrder(orderUpdateRequest);
    }
    @GetMapping("/restaurants-clients/{restaurantId}")
    public ResponseEntity<List<CustomerResponse>> getAllRestaurantClients(@RequestHeader(value = "Authorization") String token, @PathVariable Long restaurantId) {
        return orderService.getAllRestaurantClients(token, restaurantId);
    }
    @GetMapping("/all-by-owner")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByOwner(@RequestHeader(value = "Authorization") String token) {
       return orderService.getAllOrdersOrdersByOwner(token);
    }
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getCurrentOrdersByRestaurantId(@PathVariable Long restaurantId) {
        return orderService.getCurrentOrdersByRestaurantId(restaurantId);
    }
    @GetMapping("/current/user")
    public ResponseEntity<List<OrderResponse>> getCurrentOrdersByUser(@RequestHeader(value = "Authorization") String token) {
        return orderService.getCurrentOrdersByUser(token);
    }
    @GetMapping("/archived/user")
    public ResponseEntity<List<OrderResponse>> getArchivedOrdersByUser(@RequestHeader(value = "Authorization") String token) {
        return orderService.getArchivedOrdersByUser(token);
    }
    @GetMapping("/restaurants")
    public ResponseEntity<Map<String, Long>> getAllOrdersByUser(@RequestHeader(value = "Authorization") String token) {
        return orderService.getAllOrdersByUserUnified(token);
    }
}
