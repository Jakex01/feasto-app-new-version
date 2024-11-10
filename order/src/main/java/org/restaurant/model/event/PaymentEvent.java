package org.restaurant.model.event;

public record PaymentEvent(
        Long userId,
        Long orderId,
        Double amount
) {
}
