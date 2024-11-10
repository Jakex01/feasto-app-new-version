package org.restaurant.event;

import org.restaurant.model.enums.PaymentStatus;

public record PaymentProcessedEvent (
         Long orderId,
         PaymentStatus paymentStatus
){
}
