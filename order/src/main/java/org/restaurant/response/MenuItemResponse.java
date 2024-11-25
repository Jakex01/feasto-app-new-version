package org.restaurant.response;

import java.util.Map;

public record MenuItemResponse(
        Long menuItemId,
        String name,
        String category,
        String selectedSize,
        int quantity,
        String note,
        Map<String, Double> foodAdditivePrices
) {
}
