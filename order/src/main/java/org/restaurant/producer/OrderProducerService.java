package org.restaurant.producer;

import lombok.AllArgsConstructor;
import org.restaurant.model.event.NotificationEvent;
import org.restaurant.model.event.PaymentEvent;
import org.restaurant.model.event.StatisticsEvent;
import org.shared.NotificationEventOuterClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderProducerService {

    private static final String ORDER_CREATED_TOPIC = "order_created_mock";
    private static final String PROCESS_PAYMENT_TOPIC = "process_payment";
    private static final String STATISTICS_TOPIC = "process_statistics";

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendOrder(NotificationEventOuterClass.NotificationEvent orderRequest) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, orderRequest);
    }

    public void sendPaymentEvent(PaymentEvent paymentMessage) {
        kafkaTemplate.send(PROCESS_PAYMENT_TOPIC, paymentMessage);
    }
    public void sendStatisticsEvent(StatisticsEvent statisticsEvent) {
        kafkaTemplate.send(STATISTICS_TOPIC, statisticsEvent);
    }
}
