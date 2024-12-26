package org.example.consumer;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.example.model.entity.OrderDetail;
import org.example.model.enums.DeliveryOption;
import org.example.model.enums.OrderStatus;
import org.example.model.enums.PaymentStatus;
import org.example.repository.OrderDetailRepository;
import org.shared.OrderStatusOuterClass;
import org.shared.StatisticsEventWrapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderDetailRepository orderDetailRepository;

    @KafkaListener(topics = "process_statistics", groupId = "statistics-group")
    public void consumeOrderEvent(StatisticsEventWrapper.StatisticsEvent orderDetail) {
        OrderDetail orderDetail1 = OrderDetail.builder()
                .restaurantId(orderDetail.getRestaurantId())
                .orderId(orderDetail.getOrderId())
                .userEmail(orderDetail.getUserEmail())
                .createDate(toLocalDateTime(orderDetail.getCreateDate()))
                .finishedDate(toLocalDateTime(orderDetail.getFinishedDate()))
                .totalPrice(orderDetail.getTotalPrice())
                .expectedDeliveryTimeInMinutes(orderDetail.getExpectedDeliveryTimeInMinutes())
                .orderStatus(mapProtobufToDomain(orderDetail.getOrderStatus()))
                .paymentStatus(mapProtobufToDomain(orderDetail.getPaymentStatus()))
                .deliveryOption(mapProtobufToDomain(orderDetail.getDeliveryOption()))
                .build();
        orderDetailRepository.save(orderDetail1);
    }
    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    public static OrderStatus mapProtobufToDomain(OrderStatusOuterClass.OrderStatus protobufStatus) {
        return switch (protobufStatus) {
            case PENDING -> OrderStatus.PENDING;
            case ACCEPTED -> OrderStatus.ACCEPTED;
            case IN_PREPARATION -> OrderStatus.IN_PREPARATION;
            case READY_FOR_PICKUP -> OrderStatus.READY_FOR_PICKUP;
            case OUT_FOR_DELIVERY -> OrderStatus.OUT_FOR_DELIVERY;
            case DELIVERED -> OrderStatus.DELIVERED;
            case CANCELLED -> OrderStatus.CANCELLED;
            case FAILED -> OrderStatus.FAILED;
            case REFUNDED -> OrderStatus.REFUNDED;
            default -> throw new IllegalArgumentException("Unknown OrderStatus: " + protobufStatus);
        };
    }
    public static PaymentStatus mapProtobufToDomain(org.shared.StatisticsEventWrapper.PaymentStatus protobufStatus) {
        return switch (protobufStatus) {
            case PAYMENT_SUCCESSFUL -> PaymentStatus.PAYMENT_SUCCESSFUL;
            case PAYMENT_FAILED -> PaymentStatus.PAYMENT_FAILED;
            case PAYMENT_PROCESSING -> PaymentStatus.PAYMENT_PROCESSING;
            default -> throw new IllegalArgumentException("Unknown PaymentStatus: " + protobufStatus);
        };
    }
    public static DeliveryOption mapProtobufToDomain(org.shared.StatisticsEventWrapper.DeliveryOption protobufOption) {
        return switch (protobufOption) {
            case PICK_UP -> DeliveryOption.PICK_UP;
            case DELIVERY -> DeliveryOption.DELIVERY;
            default -> throw new IllegalArgumentException("Unknown DeliveryOption: " + protobufOption);
        };
    }
}

