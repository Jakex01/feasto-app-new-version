package org.restaurant.service;

import lombok.AllArgsConstructor;
import org.restaurant.model.ChatMessage;
import org.restaurant.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final MessageRepository messageRepository;
    @Override
    public void saveMessage(ChatMessage chatMessage) {
        messageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long restaurantId) {
        return messageRepository.findChatMessagesByUserIdAndRestaurantId(userId, restaurantId);
    }
}
