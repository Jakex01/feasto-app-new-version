package org.restaurant.request;

public record ChatMessageRequest(
        String message,
        String sender,
        Long restaurantId,
        String restaurantName
) {
}
