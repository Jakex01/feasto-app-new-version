package org.payments.model;

public record PaymentEvent(
        Long userId,
        Long orderId,
        Double amount
) {
}
