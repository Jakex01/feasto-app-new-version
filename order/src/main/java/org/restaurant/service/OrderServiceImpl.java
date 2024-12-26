package org.restaurant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.OrderNotFoundException;
import org.restaurant.exceptions.UserNotValidException;
import org.restaurant.mapstruct.LocationMapper;
import org.restaurant.mapstruct.MapStructMapper;
import org.restaurant.mapstruct.OrderMapper;
import org.restaurant.model.IdempotencyRecord;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.OrderLocationEntity;
import org.restaurant.model.enums.OrderStatus;
import org.restaurant.model.enums.PaymentStatus;
import org.restaurant.repository.postgres.OrderRepository;
import org.restaurant.request.OrderRequest;
import org.restaurant.request.OrderUpdateRequest;
import org.restaurant.request.OrderUserRestaurants;
import org.restaurant.request.PostOrderRequest;
import org.restaurant.response.CustomerResponse;
import org.restaurant.response.OrderResponse;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.restaurant.validators.ObjectsValidator;
import org.shared.PaymentEventWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final IdempotencyService idempotencyService;
    private final NotificationService notificationService;
    private final StatisticsService statisticsService;
    private final ObjectsValidator<OrderRequest> orderRequestObjectsValidator;
    private final ObjectsValidator<OrderUpdateRequest> orderUpdateRequestObjectsValidator;
    private final WebClientService webClientService;
    private static final String USER_URL = "http://api-gateway:8762/api/security/user";
    private static final String RESTAURANT_URL = "http://api-gateway:8762/api/restaurant/user/check-managers";

    @Override
    @Transactional
    public ResponseEntity<?> postOrder(PostOrderRequest postOrderRequest, String token, String idempotencyKey)  {
        orderRequestObjectsValidator.validate(postOrderRequest.orderRequest());
        idempotencyService.existsByIdempotencyKey(idempotencyKey);
        IdempotencyRecord newRecord = new IdempotencyRecord(idempotencyKey, 10, LocalDateTime.now());
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null) {
            throw new UserNotValidException("User not present with given credentials");
        }

        OrderEntity order = MapStructMapper.INSTANCE.requestToEntity(postOrderRequest.orderRequest());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUserEmail(userEmail);
        order.setExpectedDeliveryTimeInMinutes(5);

        if (postOrderRequest.orderLocationRequest() != null) {
            OrderLocationEntity orderLocation = LocationMapper.INSTANCE.locationRequestToEntity(postOrderRequest.orderLocationRequest());
            order.setLocation(orderLocation);
        }
        orderRepository.save(order);
        idempotencyService.saveIdempotencyKey(newRecord);
        notificationService.sendFileToNotification(postOrderRequest.orderRequest(), userEmail);
        statisticsService.sendOrderToStatistics(order);
//        paymentService.sendOrderToPayment(userEmail, orderRequest.totalPrice(), orderEntity.getId());
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
    public ResponseEntity<List<OrderResponse>> getCurrentOrdersByRestaurantId(Long restaurantId) {
        List<OrderResponse> orders = orderRepository
                .findAllByRestaurantId(restaurantId)
                .stream().map(OrderMapper.INSTANCE::orderEntityToResponse)
                .toList();
        return ResponseEntity.ok(orders);
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
                .map(OrderMapper.INSTANCE::orderEntityToResponse)
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
                .map(OrderMapper.INSTANCE::orderEntityToResponse)
                .toList();
        return ResponseEntity.ok(orderResponses);
    }

    @Override
    public ResponseEntity<Map<String, Long>> getAllOrdersByUserUnified(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();

        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }
        Map<String, Long> orderEntities = orderRepository
                .findAllByUserEmail(userEmail)
                .stream()
                .map(order -> new OrderUserRestaurants(order.getRestaurantId(), order.getRestaurantName()))
                .distinct()
                .collect(Collectors.toMap(
                        OrderUserRestaurants::restaurantName,
                        OrderUserRestaurants::restaurantId,
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.ok(orderEntities);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getAllOrdersOrdersByOwner(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();

        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }
        Set<Long> restaurantIds = orderRepository
                .findAll()
                .stream().map(OrderMapper.INSTANCE::orderEntityToResponse)
                .map(OrderResponse::restaurantId)
                .collect(Collectors.toSet());

        Map<Long, Boolean> isManagerMap = webClientService
                .fetchIsManagerMap(RESTAURANT_URL, restaurantIds, jwtToken)
                .block();

        if (isManagerMap == null) {
            throw new IllegalStateException("Failed to retrieve manager data for restaurants");
        }
        List<OrderResponse> filteredOrders = orderRepository.findAll().stream()
                .map(OrderMapper.INSTANCE::orderEntityToResponse)
                .filter(order -> isManagerMap.getOrDefault(order.restaurantId(), false))
                .toList();

        return ResponseEntity.ok(filteredOrders);
    }

    @Override
    public ResponseEntity<List<CustomerResponse>> getAllRestaurantClients(String token, Long restaurantId) {
        List<OrderEntity> orders = orderRepository.findAllByRestaurantId(restaurantId);
        Map<String, List<OrderEntity>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(OrderEntity::getUserEmail));
        List<CustomerResponse> customerResponses = groupedOrders.entrySet().stream()
                .map(entry -> {
                    String userEmail = entry.getKey();
                    List<OrderEntity> userOrders = entry.getValue();
                    int ordersCount = userOrders.size();
                    LocalDateTime lastOrder = userOrders.stream()
                            .map(OrderEntity::getCreateDate)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return new CustomerResponse(userEmail, ordersCount, lastOrder);
                })
                .toList();

        return ResponseEntity.ok(customerResponses);
    }

    @KafkaListener(topics = "process_payment", groupId = "order-group")
    @Transactional
    public void handlePaymentProcessedEvent(PaymentEventWrapper.PaymentEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> order.setPaymentStatus(mapToInternal(event.getPaymentStatus())));
    }

    public static PaymentStatus mapToInternal(PaymentEventWrapper.PaymentEvent.PaymentStatus protoStatus) {
        if (protoStatus == null) {
            throw new IllegalArgumentException("Protobuf PaymentStatus cannot be null");
        }
        switch (protoStatus) {
            case PAYMENT_SUCCESSFUL:
                return PaymentStatus.PAYMENT_SUCCESSFUL;
            case PAYMENT_FAILED:
                return PaymentStatus.PAYMENT_FAILED;
            case UNRECOGNIZED:
            default:
                throw new IllegalArgumentException("Unsupported Protobuf PaymentStatus: " + protoStatus);
        }
    }


}
