package org.restaurant.repository;

import org.restaurant.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByUserEmailAndRestaurantId(String userEmail, Long restaurantId);
    List<ChatMessage> findAllByUserEmail(String userEmail);
}
