package org.restaurant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.event.PaymentProcessedEvent;
import org.restaurant.exceptions.OrderNotFoundException;
import org.restaurant.exceptions.UserNotValidException;
import org.restaurant.mapstruct.MapStructMapper;
import org.restaurant.mapstruct.MenuItemMapper;
import org.restaurant.model.IdempotencyRecord;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.enums.OrderStatus;
import org.restaurant.repository.postgres.OrderRepository;
import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.response.MenuItemResponse;
import org.restaurant.response.OrderResponse;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.restaurant.validators.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


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
    private static final String USER_URL = "http://localhost:8762/api/user";

    @Override
    @Transactional
    public ResponseEntity<?> postOrder(OrderRequest orderRequest, String token, String idempotencyKey)  {
        orderRequestObjectsValidator.validate(orderRequest);
        idempotencyService.existsByIdempotencyKey(idempotencyKey);
        IdempotencyRecord newRecord = new IdempotencyRecord(idempotencyKey, 10, LocalDateTime.now());
        idempotencyService.saveIdempotencyKey(newRecord);
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }

        OrderEntity order = MapStructMapper.INSTANCE.requestToEntity(orderRequest);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUserEmail(userEmail);
        order.setExpectedDeliveryTimeInMinutes(5);
        OrderEntity orderEntity =  orderRepository.save(order);
        notificationService.sendFileToNotification(orderRequest, token);
        statisticsService.sendOrderToStatistics(order);
        paymentService.sendOrderToPayment(userEmail, orderRequest.totalPrice(), orderEntity.getId());
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateOrder(OrderUpdateRequest orderUpdateRequest) {
        orderUpdateRequestObjectsValidator.validate(orderUpdateRequest);
        OrderEntity order = orderRepository.findById(orderUpdateRequest.orderId())
                .orElseThrow(()-> new OrderNotFoundException("order not found"));
        order.setOrderStatus(orderUpdateRequest.orderStatus());
        statisticsService.sendUpdateOrderToStatistics(orderUpdateRequest);
        return ResponseEntity.ok(order.getOrderStatus());
    }

    @Override
    public ResponseEntity<?> getCurrentOrdersByRestaurantId(Long restaurantId) {
        List<OrderEntity> orders = orderRepository.findAllByRestaurantId(restaurantId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> {
                    List<MenuItemResponse> menuItemResponses = order.getItems()
                            .stream()
                            .map(MenuItemMapper.INSTANCE::menuEntityToResponse)
                            .toList();
                    return new OrderResponse(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getOrderStatus(),
                            menuItemResponses
                    );
                })
                .toList();
        return ResponseEntity.ok(orderResponses);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getCurrentOrdersByUser(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();

        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }
        List<OrderEntity> orders = orderRepository.findAllByUserEmail(userEmail);
        List<OrderResponse> orderResponses = orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING
                        || order.getOrderStatus() == OrderStatus.ACCEPTED
                        || order.getOrderStatus() == OrderStatus.IN_PREPARATION
                        || order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .map(order -> {
                    List<MenuItemResponse> menuItemResponses = order.getItems()
                            .stream()
                            .map(MenuItemMapper.INSTANCE::menuEntityToResponse)
                            .toList();
                    return new OrderResponse(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getOrderStatus(),
                            menuItemResponses
                    );
                })
                .toList();
        return ResponseEntity.ok(orderResponses);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getArchivedOrdersByUser(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();

        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }
        List<OrderEntity> orders = orderRepository.findAllByUserEmail(userEmail);
        List<OrderResponse> orderResponses = orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.READY_FOR_PICKUP
                        || order.getOrderStatus() == OrderStatus.DELIVERED
                        || order.getOrderStatus() == OrderStatus.CANCELLED
                        || order.getOrderStatus() == OrderStatus.FAILED)
                .map(order -> {
                    List<MenuItemResponse> menuItemResponses = order.getItems()
                            .stream()
                            .map(MenuItemMapper.INSTANCE::menuEntityToResponse)
                            .toList();
                    return new OrderResponse(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getOrderStatus(),
                            menuItemResponses
                    );
                })
                .toList();
        return ResponseEntity.ok(orderResponses);
    }

    @KafkaListener(topics = "payment_processed", groupId = "order-group")
    @Transactional
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> order.setPaymentStatus(event.paymentStatus()));
    }




}
