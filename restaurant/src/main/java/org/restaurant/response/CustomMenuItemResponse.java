package org.restaurant.response;

import org.restaurant.model.enums.FoodAdditive;
import org.restaurant.model.enums.FoodCategory;
import org.restaurant.request.SizesWithPrices;

import java.util.List;
import java.util.Map;

public record CustomMenuItemResponse(
        Long menuItemId,
        String name,
        String description,
        boolean available,
        FoodCategory category,
        Map<FoodAdditive, Double> foodAdditivePrices,
        List<SizesWithPrices> sizesWithPrices


) {
}
