package org.restaurant.service;

import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.request.PostOrderRequest;
import org.restaurant.response.CustomerResponse;
import org.restaurant.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface OrderService {
    ResponseEntity<?> postOrder(PostOrderRequest postOrderRequest, String token, String idempotencyKey) throws IOException, URISyntaxException;

    ResponseEntity<?> updateOrder(OrderUpdateRequest orderUpdateRequest);

    ResponseEntity<List<OrderResponse>> getCurrentOrdersByRestaurantId(Long restaurantId);

    ResponseEntity<List<OrderResponse>> getCurrentOrdersByUser(String token);

    ResponseEntity<List<OrderResponse>> getArchivedOrdersByUser(String token);

    ResponseEntity<Map<String, Long>> getAllOrdersByUserUnified(String token);

    ResponseEntity<List<OrderResponse>> getAllOrdersOrdersByOwner(String token);


    ResponseEntity<List<CustomerResponse>> getAllRestaurantClients(String token, Long restaurantId);
}
