package org.restaurant.request;

import jakarta.persistence.Enumerated;
import org.restaurant.model.enums.OrderStatus;

public record OrderUpdateRequest(
    Long restaurantId,
    OrderStatus orderStatus
) {
}
