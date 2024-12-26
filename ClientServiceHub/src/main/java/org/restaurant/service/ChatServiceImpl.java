package org.restaurant.service;

import lombok.AllArgsConstructor;
import org.restaurant.configuration.UrlConfig;
import org.restaurant.model.ChatMessage;
import org.restaurant.repository.MessageRepository;
import org.restaurant.request.ChatMessageRequest;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final MessageRepository messageRepository;
    private final UserDetailsClient client;
    private final UrlConfig urlConfig;
    @Override
    public void saveMessage(ChatMessageRequest chatMessageRequest, String token) {
        String userEmail = client.fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();
        if(userEmail == null) {
            throw new NullPointerException("User not present");
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatMessageRequest.message())
                .sender(chatMessageRequest.sender())
                .userEmail(userEmail)
                .timestamp(LocalDateTime.now())
                .restaurantId(chatMessageRequest.restaurantId())
                .restaurantName(chatMessageRequest.restaurantName())
                .build();
        messageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatHistory(String token, Long restaurantId) {
        String userEmail = client.fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();
        if(userEmail == null) {
            throw new NullPointerException("User not present");
        }
        List<ChatMessage> chatHistory = messageRepository.findAllByUserEmailAndRestaurantId(userEmail, restaurantId);
        return chatHistory != null ? chatHistory : new ArrayList<>();
    }

    @Override
    public Map<String, Long> getAllChats(String token) {
        String userEmail = client.fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();
        if(userEmail == null) {
        throw new NullPointerException("User not present");
    }
        List<ChatMessage> chatMessages = messageRepository.findAllByUserEmail(userEmail);

        return chatMessages.stream()
                .collect(Collectors.toMap(ChatMessage::getRestaurantName, ChatMessage::getRestaurantId,
                        (existing, replacement) -> existing));
    }
}
