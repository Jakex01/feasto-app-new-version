package org.restaurant.response;

import org.restaurant.model.enums.FoodCategory;
import org.restaurant.request.FoodAdditivePriceRequest;
import org.restaurant.request.SizesWithPrices;

import java.util.List;

public record CustomMenuItemResponse(
        Long menuItemId,
        String name,
        String description,
        boolean available,
        FoodCategory category,
        List<FoodAdditivePriceRequest> foodAdditivePrices,
        List<SizesWithPrices> sizesWithPrices


) {
}
