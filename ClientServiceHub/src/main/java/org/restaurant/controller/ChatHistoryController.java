package org.restaurant.controller;

import lombok.AllArgsConstructor;
import org.restaurant.model.ChatMessage;
import org.restaurant.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat/static")
public class ChatHistoryController {

    private final ChatService chatService;

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(@RequestHeader(value = "Authorization") String token, @RequestParam Long restaurantId) {
        return chatService.getChatHistory(token, restaurantId);
    }
    @GetMapping("/all")
    public Map<String, Long> getAllChats(@RequestHeader(value = "Authorization") String token) {
        return chatService.getAllChats(token);
    }

}
