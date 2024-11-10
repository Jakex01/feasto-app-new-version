package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.model.event.PaymentEvent;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebClientService webClientService;
    private final OrderProducerService orderProducerService;
    private static final String USER_URL = "http://localhost:8762/api/auth/user";

    @Async
    public void sendOrderToPayment(String token, double amount, long orderId) {
        String jwtToken = JwtUtil.extractToken(token);
        Long userId = webClientService.fetchData(USER_URL, Long.class, jwtToken).block();
        PaymentEvent paymentEvent = new PaymentEvent(userId,orderId, amount);
        orderProducerService.sendPaymentEvent(paymentEvent);

    }
}
