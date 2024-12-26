package org.restaurant.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        String restaurantName,
        double rating,
        String review,
        LocalDateTime createDate
) {
}
