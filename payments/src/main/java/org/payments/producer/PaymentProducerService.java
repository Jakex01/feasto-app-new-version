package org.payments.producer;

import lombok.AllArgsConstructor;
import org.shared.PaymentEventWrapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentProducerService {
    private static final String PROCESS_PAYMENT_TOPIC = "process_payment";

    private final KafkaTemplate<String,PaymentEventWrapper.PaymentEvent> paymentEventKafkaTemplate;
    public void sendOrder(PaymentEventWrapper.PaymentEvent paymentEvent) {
        paymentEventKafkaTemplate.send(PROCESS_PAYMENT_TOPIC, paymentEvent);
    }
}
