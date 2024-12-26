package org.restaurant.scheduler;

import lombok.AllArgsConstructor;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.enums.PaymentStatus;
import org.restaurant.repository.postgres.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class PaymentStatusScheduler {
    private final OrderRepository orderRepository;
    private static final int PAYMENT_TIMEOUT_MINUTES = 15;

    @Scheduled(fixedRate = 600000)
    public void checkAndUpdatePaymentStatus() {
        List<OrderEntity> processingOrders = orderRepository
                .findByPaymentStatus(PaymentStatus.PAYMENT_PROCESSING);
        LocalDateTime now = LocalDateTime.now();
        for (OrderEntity order : processingOrders) {
            if (order.getCreateDate().plusMinutes(PAYMENT_TIMEOUT_MINUTES).isBefore(now)) {
                order.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
                orderRepository.save(order);
            }
        }
    }
}
