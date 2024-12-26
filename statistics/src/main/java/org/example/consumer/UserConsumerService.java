//package org.example.consumer;
//
//import lombok.AllArgsConstructor;
//import org.example.util.UserOrderService;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class UserConsumerService {
//    private final UserOrderService userOrderService;
//    private static final String EMAIL_NOTIFICATION_TOPIC = "users_check";
//
//    @KafkaListener(topics = EMAIL_NOTIFICATION_TOPIC, groupId = "user_group")
//    public void checkUsersOrders(List<String> usersEvent) {
//        userOrderService.checkUsersOrders(usersEvent);
//    }
//
//}
