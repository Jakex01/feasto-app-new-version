package org.restaurant.request;

import jakarta.validation.constraints.NotNull;

public record PostRatingRequest(
        @NotNull(message = "rating can't be null")
        double rating,
        String review,
        @NotNull(message = "restaurantId can't be null")
        Long restaurantId
){
}
