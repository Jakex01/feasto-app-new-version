package org.restaurant.service;

import org.restaurant.model.MessageEntity;
import org.restaurant.request.ConversationRequest;
import org.restaurant.response.ConversationResponse;
import org.restaurant.response.RestaurantConversationsResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface ConversationService {
    ResponseEntity<?> postConversation(ConversationRequest conversationRequest);

    ResponseEntity<List<ConversationResponse>> getAllConversationsByRestaurantAndUser(String token, Long restaurantId);

    ResponseEntity<Set<RestaurantConversationsResponse>> getAllRestaurantsWithConversationByUser(String token);

    ResponseEntity<?> deleteConversationById(String token, Long conversationId);

    ResponseEntity<List<MessageEntity>> getConversationById(Long conversationId);
}
