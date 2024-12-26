package org.restaurant.request;

public record PostOrderRequest(
        OrderRequest orderRequest,
        OrderLocationRequest orderLocationRequest
) {
}
