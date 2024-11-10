package org.restaurant.request;

import org.restaurant.model.enums.MessageOwner;

public record MessageRequest(
        MessageOwner messageOwner,
        String content,
        Long conversationId
) {
}
