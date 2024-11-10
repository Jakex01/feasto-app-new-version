package org.example.model.event;

import org.example.model.enums.DeliveryOption;

import java.time.LocalDateTime;

public record StatisticsEvent(
        Long restaurantId,
        LocalDateTime orderDate,
        Double totalPrice,
        DeliveryOption deliveryOption
) {
}
