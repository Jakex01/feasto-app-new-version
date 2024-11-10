package org.notification.consumer;

import lombok.RequiredArgsConstructor;
import org.notification.service.EmailService;
import org.shared.NotificationEventOuterClass;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final EmailService emailService;

    @KafkaListener(topics = "order_created_mock", groupId = "notification_group")
    public void sendOrderConfirmation(NotificationEventOuterClass.NotificationEvent orderRequest) {
        emailService.sendEmailWithPdf(orderRequest);
    }


}
