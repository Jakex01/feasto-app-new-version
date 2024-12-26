package org.payments.model;

public record PaymentEvent(
        String userEmail,
        String restaurantName,
        Long orderId,
        String currency,
        Double amount
) {
}
