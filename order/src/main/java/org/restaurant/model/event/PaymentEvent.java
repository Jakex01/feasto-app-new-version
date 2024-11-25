package org.restaurant.model.event;

public record PaymentEvent(
        String userEmail,
        Long orderId,
        Double amount
) {
}
