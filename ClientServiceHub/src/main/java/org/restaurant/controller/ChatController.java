package org.restaurant.controller;

import lombok.AllArgsConstructor;
import org.restaurant.model.ChatMessage;
import org.restaurant.request.ChatMessageRequest;
import org.restaurant.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessageRequest sendMessage(
        @Payload ChatMessageRequest chatMessage,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        String token = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("token");
        chatService.saveMessage(chatMessage, token);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
