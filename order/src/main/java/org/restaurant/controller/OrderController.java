package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.response.OrderResponse;
import org.restaurant.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/order",
produces = MediaType.APPLICATION_JSON_VALUE,
consumes = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @SneakyThrows
    public ResponseEntity<?> postOrder(@RequestBody OrderRequest orderRequest,
                                       @RequestHeader(value = "Authorization") String token,
                                       @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
        return orderService.postOrder(orderRequest, token, idempotencyKey);
    }
    @PatchMapping
    public ResponseEntity<?> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest){
        return orderService.updateOrder(orderUpdateRequest);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> getCurrentOrdersByRestaurantId(@PathVariable Long restaurantId) {
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
}
