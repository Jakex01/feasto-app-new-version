package org.restaurant.service;

import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface OrderService {
    ResponseEntity<?> postOrder(OrderRequest orderRequest, String token, String idempotencyKey) throws IOException, URISyntaxException;

    ResponseEntity<?> updateOrder(OrderUpdateRequest orderUpdateRequest);

    ResponseEntity<?> getCurrentOrdersByRestaurantId(Long restaurantId);

    ResponseEntity<List<OrderResponse>> getCurrentOrdersByUser(String token);

    ResponseEntity<List<OrderResponse>> getArchivedOrdersByUser(String token);
}
