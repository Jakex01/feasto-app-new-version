package org.payments.consumer;

import lombok.RequiredArgsConstructor;
import org.payments.model.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private static final String PROCESS_PAYMENT = "process_payment";

    @KafkaListener(topics = PROCESS_PAYMENT, groupId = "payment_group")
    public void sendOrderConfirmation(PaymentEvent paymentEvent) {

    }
}
