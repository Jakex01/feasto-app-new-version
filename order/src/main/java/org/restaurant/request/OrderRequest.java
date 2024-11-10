package org.restaurant.request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.restaurant.model.enums.DeliveryOption;

import java.util.List;

public record OrderRequest(
        List<MenuItemOrderRequest> items,
        Double totalPrice,
        @Enumerated(EnumType.STRING)
         DeliveryOption deliveryOption,
        Long restaurantId,
        String restaurantName,
        String orderNote,
        Double tip,
        Double deliveryFee
) {
}
