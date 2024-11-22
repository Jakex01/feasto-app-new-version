package org.example.consumer;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.OrderDetail;
import org.example.repository.OrderDetailRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderDetailRepository orderDetailRepository;

    @KafkaListener(topics = "order-events", groupId = "statistics-group")
    public void consumeOrderEvent(OrderDetail orderDetail) {
        orderDetailRepository.save(orderDetail);
    }
}
