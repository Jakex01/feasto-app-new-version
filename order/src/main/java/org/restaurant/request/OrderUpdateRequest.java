package org.restaurant.request;

import org.restaurant.model.enums.OrderStatus;

public record OrderUpdateRequest(
    Long orderId,
    OrderStatus orderStatus
) {
}
