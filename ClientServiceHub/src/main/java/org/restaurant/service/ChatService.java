package org.restaurant.service;

import org.restaurant.model.ChatMessage;

import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(Long userId, Long restaurantId);
}
