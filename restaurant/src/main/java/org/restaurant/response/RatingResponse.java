package org.restaurant.response;

import java.time.LocalDateTime;

public record RatingResponse(
        double rating,
        String review,
        LocalDateTime createDate

) {
}
