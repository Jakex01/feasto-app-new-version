package org.restaurant.producer;

import lombok.AllArgsConstructor;
import org.shared.NotificationEventOuterClass;
import org.shared.OrderUpdateEventWrapper;
import org.shared.PaymentEventWrapper;
import org.shared.StatisticsEventWrapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderProducerService {

    private static final String ORDER_CREATED_TOPIC = "order_created_mock";
    private static final String PROCESS_PAYMENT_TOPIC = "process_payment";
    private static final String STATISTICS_TOPIC = "process_statistics";
    private static final String ORDER_UPDATE_TOPIC = "order_update";

    private final KafkaTemplate<String, NotificationEventOuterClass.NotificationEvent> notificationKafkaTemplate;
    private final KafkaTemplate<String, StatisticsEventWrapper.StatisticsEvent> statisticsKafkaTemplate;

    public void sendMessage() {
        NotificationEventOuterClass.NotificationEvent notificationEvent = NotificationEventOuterClass.NotificationEvent.newBuilder()
                .setEmail("hello world")
                .build();

        notificationKafkaTemplate.send("funny", notificationEvent);
    }

    public void sendOrder(NotificationEventOuterClass.NotificationEvent orderRequest) {
        notificationKafkaTemplate.send(ORDER_CREATED_TOPIC, orderRequest);
    }

    public void sendPaymentEvent(PaymentEventWrapper.PaymentEvent paymentMessage) {
//        kafkaTemplate.send(PROCESS_PAYMENT_TOPIC, paymentMessage);
    }
    public void sendStatisticsEvent(StatisticsEventWrapper.StatisticsEvent statisticsEvent) {
        statisticsKafkaTemplate.send(STATISTICS_TOPIC, statisticsEvent);
    }
    public void updateOrderEvent(OrderUpdateEventWrapper.OrderUpdateEvent orderUpdateEvent) {
//        kafkaTemplate.send(ORDER_UPDATE_TOPIC, orderUpdateEvent);
    }
}
