package org.restaurant.response;

import java.time.LocalDateTime;
import java.util.List;

public record RestaurantResponse(

        Long restaurantId,
        String name,
        String description,
        String phoneNumber,
        String openingHours,
        double rating,
        String foodType,
        String image,
        LocalDateTime createDate,
        List<LocationResponse> locations,
        List<MenuItemResponse> menuItems,
        long commentsCount
) {
}
