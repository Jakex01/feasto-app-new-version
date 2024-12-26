package org.restaurant.response;

import java.time.LocalDateTime;

public record CustomerResponse(
        String userEmail,
        Integer ordersCount,
        LocalDateTime lastOrder
) {
}
