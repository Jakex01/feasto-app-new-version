package org.restaurant.request;

import java.util.List;
public record PostMenuItemRequest(
        String name,
        String description,
        boolean available,
        String foodCategory,
        List<SizesWithPrices> sizesWithPrices
) {
}
