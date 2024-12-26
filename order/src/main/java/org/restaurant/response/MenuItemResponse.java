package org.restaurant.response;

import java.util.Map;

public record MenuItemResponse(
        Long menuItemId,
        String name,
        String category,
        String selectedSize,
        String selectedPrice,
        String totalItemPrice,
        int quantity,
        Map<String, Double> foodAdditivePrices
) {
}
