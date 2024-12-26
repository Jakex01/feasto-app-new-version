package org.restaurant.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRatingRequest(
        @NotNull(message = "Rating can't be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
        @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be at most 5.0")
        double rating,

        @Size(max = 255, message = "Review cannot exceed 255 characters")
        String review,

        @NotNull(message = "Restaurant ID can't be null")
        Long restaurantId
) {
}
