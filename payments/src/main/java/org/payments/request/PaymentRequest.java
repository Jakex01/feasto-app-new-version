package org.payments.request;

public record PaymentRequest(
        double amount,
        String method,
        String currency,
        String description
) {
}
