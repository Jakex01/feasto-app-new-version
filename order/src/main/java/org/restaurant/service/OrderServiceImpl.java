package org.restaurant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.event.PaymentProcessedEvent;
import org.restaurant.mapstruct.MapStructMapper;
import org.restaurant.mapstruct.OrderMapper;
import org.restaurant.model.IdempotencyRecord;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.enums.OrderStatus;
import org.restaurant.model.event.StatisticsEvent;
import org.restaurant.repository.postgres.OrderRepository;
import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.restaurant.validators.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final IdempotencyService idempotencyService;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final StatisticsService statisticsService;
    private final ObjectsValidator<OrderRequest> orderRequestObjectsValidator;
    private final ObjectsValidator<OrderUpdateRequest> orderUpdateRequestObjectsValidator;
    private final WebClientService webClientService;
    private static final String USER_URL = "http://localhost:8762/api/auth/user";

    @Override
    @Transactional
    public ResponseEntity<?> postOrder(OrderRequest orderRequest, String token, String idempotencyKey)  {
        orderRequestObjectsValidator.validate(orderRequest);
        idempotencyService.existsByIdempotencyKey(idempotencyKey);
        IdempotencyRecord newRecord = new IdempotencyRecord(idempotencyKey, 10, LocalDateTime.now());
        idempotencyService.saveIdempotencyKey(newRecord);
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        OrderEntity order = MapStructMapper.INSTANCE.requestToEntity(orderRequest);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUserEmail(userEmail);
        order.setExpectedDeliveryTimeInMinutes(5);
        OrderEntity orderEntity =  orderRepository.save(order);
//        StatisticsEvent statisticsEvent = OrderMapper.INSTANCE
//                .orderEntityToStatisticsEvent(orderEntity);
        notificationService.SendFileToNotification(orderRequest, token);
//        statisticsService.sendOrderToStatistics(statisticsEvent);
//        paymentService.sendOrderToPayment(token, orderRequest.totalPrice(), orderEntity.getId());
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateOrder(OrderUpdateRequest orderUpdateRequest) {
        orderUpdateRequestObjectsValidator.validate(orderUpdateRequest);
        OrderEntity order = orderRepository.findByRestaurantId(orderUpdateRequest.restaurantId());
        order.setOrderStatus(orderUpdateRequest.orderStatus());
        return ResponseEntity.ok(order.getOrderStatus());
    }

    @KafkaListener(topics = "payment_processed", groupId = "order-group")
    @Transactional
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> order.setPaymentStatus(event.paymentStatus()));
    }




}
