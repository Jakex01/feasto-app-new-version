package org.restaurant.service;

import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;

public interface OrderService {
    ResponseEntity<?> postOrder(OrderRequest orderRequest, String token, String idempotencyKey) throws IOException, URISyntaxException;

    ResponseEntity<?> updateOrder(OrderUpdateRequest orderUpdateRequest);
}
