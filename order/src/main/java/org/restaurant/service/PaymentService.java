package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.util.WebClientService;
import org.shared.PaymentEventWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebClientService webClientService;
    private final OrderProducerService orderProducerService;
    private static final String USER_URL = "http://localhost:8762/api/user";

    @Async
    public void sendOrderToPayment(String userEmail, double amount, long orderId) {
        PaymentEventWrapper.PaymentEvent paymentEvent = PaymentEventWrapper
                .PaymentEvent
                .newBuilder()
                .setOrderId(orderId)
                .setAmount(amount)
                .setUserEmail(userEmail)
                .build();
        orderProducerService.sendPaymentEvent(paymentEvent);

    }
}
