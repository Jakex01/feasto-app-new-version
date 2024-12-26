package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.producer.OrderProducerService;
import org.shared.PaymentEventWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderProducerService orderProducerService;

    @Async
    public void sendOrderToPayment(String userEmail, double amount, long orderId) {
        PaymentEventWrapper.PaymentEvent paymentEvent = PaymentEventWrapper
                .PaymentEvent
                .newBuilder()
                .setOrderId(orderId)
                .setUserEmail(userEmail)
                .build();
        orderProducerService.sendPaymentEvent(paymentEvent);

    }
}
