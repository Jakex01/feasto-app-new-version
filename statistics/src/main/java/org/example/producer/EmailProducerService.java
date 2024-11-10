package org.example.producer;

import lombok.AllArgsConstructor;
import org.example.model.event.EmailReminderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailProducerService {
    private static final String EMAIL_ORDER_REMINDER = "email_reminder";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailReminder(List<EmailReminderEvent> event) {
        kafkaTemplate.send(EMAIL_ORDER_REMINDER, event);
    }
}
