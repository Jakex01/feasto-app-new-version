package org.restaurant.controller;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

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
}
