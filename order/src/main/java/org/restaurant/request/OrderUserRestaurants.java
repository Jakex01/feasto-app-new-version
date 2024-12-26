package org.restaurant.request;

public record OrderUserRestaurants(
        Long restaurantId,
        String restaurantName
) {
}
