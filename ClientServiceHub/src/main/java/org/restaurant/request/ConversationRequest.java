package org.restaurant.request;

public record ConversationRequest(
        Long userId,
        Long restaurantId,
        String name
) {
}
