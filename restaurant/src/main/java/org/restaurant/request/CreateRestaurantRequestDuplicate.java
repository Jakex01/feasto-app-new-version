package org.restaurant.request;

import java.util.List;

public record CreateRestaurantRequestDuplicate(
        CreateRestaurantRequest restaurantInfo,
        PostLocationRequest restaurantLocation,
        List<PostMenuItemRequest> restaurantMenuItems
) {
}
