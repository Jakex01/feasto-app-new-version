package org.restaurant.response;

import org.restaurant.model.enums.FoodCategory;




public record MenuItemResponse(
        Long menuItemId,
        String name,
        String description,
        boolean available,
        FoodCategory foodCategory,

        Double lowestPrice
) {
}
