package org.restaurant.service;

import org.restaurant.model.ChatMessage;
import org.restaurant.request.ChatMessageRequest;

import java.util.List;
import java.util.Map;

public interface ChatService {
    void saveMessage(ChatMessageRequest chatMessage, String token);
    List<ChatMessage> getChatHistory(String userEmail, Long restaurantId);

    Map<String, Long> getAllChats(String token);
}
