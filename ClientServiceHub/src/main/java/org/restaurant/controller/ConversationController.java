package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.restaurant.model.MessageEntity;
import org.restaurant.request.ConversationRequest;
import org.restaurant.response.ConversationResponse;
import org.restaurant.response.RestaurantConversationsResponse;
import org.restaurant.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/conversation")
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getAllConversationsByRestaurantAndUser(@RequestHeader(value = "Authorization") String token,
                                                                                             @RequestParam Long restaurantId) {
        return conversationService.getAllConversationsByRestaurantAndUser(token,restaurantId);
    }
    @GetMapping
    public ResponseEntity<Set<RestaurantConversationsResponse>> getAllRestaurantsWithConversationByUser(
            @RequestHeader(value = "Authorization") String token) {
        return conversationService.getAllRestaurantsWithConversationByUser(token);
    }
    @GetMapping
    public ResponseEntity<List<MessageEntity>> getConversationById(@RequestParam Long conversationId) {
        return conversationService.getConversationById(conversationId);
    }
    @PostMapping
    public ResponseEntity<?> postConversation(@RequestBody ConversationRequest conversationRequest) {
        return conversationService.postConversation(conversationRequest);
    }
    @DeleteMapping
    public ResponseEntity<?> deleteConversationById(@RequestHeader(value = "Authorization") String token,
                                                    @RequestParam Long conversationId) {
        return conversationService.deleteConversationById(token, conversationId);
    }

}
