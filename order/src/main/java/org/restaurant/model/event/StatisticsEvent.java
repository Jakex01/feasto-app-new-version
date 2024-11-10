package org.restaurant.model.event;

import org.restaurant.model.enums.DeliveryOption;
import java.time.LocalDateTime;

public record StatisticsEvent(
        Long restaurantId,
        LocalDateTime orderDate,
        Double totalPrice,
        DeliveryOption deliveryOption
) {
}
