package org.restaurant.controller;

import lombok.AllArgsConstructor;
import org.restaurant.model.ChatMessage;
import org.restaurant.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
public class ChatHistoryController {

    private final ChatService chatService;

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(@RequestParam Long userId, @RequestParam Long restaurantId) {
        return chatService.getChatHistory(userId, restaurantId);
    }

}
