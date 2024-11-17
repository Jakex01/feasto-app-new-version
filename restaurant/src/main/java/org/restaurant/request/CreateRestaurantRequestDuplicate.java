package org.restaurant.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRestaurantRequestDuplicate(
        @NotNull
        @Valid
        CreateRestaurantRequest restaurantInfo,
        @NotNull
        @Valid
        PostLocationRequest restaurantLocation,
        @NotNull
        @Valid
        List<PostMenuItemRequest> restaurantMenuItems
) {
}
