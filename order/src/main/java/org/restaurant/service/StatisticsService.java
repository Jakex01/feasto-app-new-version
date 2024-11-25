package org.restaurant.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.enums.DeliveryOption;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.request.OrderUpdateRequest;
import org.shared.OrderStatusOuterClass;
import org.shared.OrderUpdateEventWrapper;
import org.shared.StatisticsEventWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderProducerService orderProducerService;
    @Async
    public void sendOrderToStatistics(OrderEntity order) {
        StatisticsEventWrapper.StatisticsEvent statisticsEvent = StatisticsEventWrapper
                .StatisticsEvent
                .newBuilder()
                .setOrderId(order.getId())
                .setUserEmail(order.getUserEmail())
                .setRestaurantId(order.getRestaurantId())
                .setCreateDate(toProtobufTimestamp(order.getCreateDate()))
                .setFinishedDate(toProtobufTimestamp(order.getCreateDate()))
                .setTotalPrice(order.getTotalPrice())
                .setExpectedDeliveryTimeInMinutes(order.getExpectedDeliveryTimeInMinutes())
                .setOrderStatus(toProtobufOrderStatus(order.getOrderStatus()))
                .setPaymentStatus(toProtobufPaymentStatus(order.getPaymentStatus()))
                .setDeliveryOption(toProtobufDeliveryOption(order.getDeliveryOption()))
                .build();

        orderProducerService.sendStatisticsEvent(statisticsEvent);
    }
    @Async
    public void sendUpdateOrderToStatistics(OrderUpdateRequest orderUpdateRequest) {
        OrderUpdateEventWrapper.OrderUpdateEvent orderUpdateEvent = OrderUpdateEventWrapper.OrderUpdateEvent
                .newBuilder()
                .setOrderId(orderUpdateRequest.orderId())
                .setOrderStatus(toProtobufOrderStatus(orderUpdateRequest.orderStatus()))
                .build();
        orderProducerService.updateOrderEvent(orderUpdateEvent);
    }
    public static Timestamp toProtobufTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return Timestamp.getDefaultInstance();
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
    public static OrderStatusOuterClass.OrderStatus toProtobufOrderStatus(org.restaurant.model.enums.OrderStatus orderStatus) {
        if (orderStatus == null) {
            return OrderStatusOuterClass.OrderStatus.UNRECOGNIZED;
        }

        switch (orderStatus) {
            case PENDING:
                return OrderStatusOuterClass.OrderStatus.PENDING;
            case ACCEPTED:
                return OrderStatusOuterClass.OrderStatus.ACCEPTED;
            case IN_PREPARATION:
                return OrderStatusOuterClass.OrderStatus.IN_PREPARATION;
            case READY_FOR_PICKUP:
                return OrderStatusOuterClass.OrderStatus.READY_FOR_PICKUP;
            case OUT_FOR_DELIVERY:
                return OrderStatusOuterClass.OrderStatus.OUT_FOR_DELIVERY;
            case DELIVERED:
                return OrderStatusOuterClass.OrderStatus.DELIVERED;
            case CANCELLED:
                return OrderStatusOuterClass.OrderStatus.CANCELLED;
            case FAILED:
                return OrderStatusOuterClass.OrderStatus.FAILED;
            case REFUNDED:
                return OrderStatusOuterClass.OrderStatus.REFUNDED;
            default:
                throw new IllegalArgumentException("Unknown OrderStatus: " + orderStatus);
        }
    }


    public static StatisticsEventWrapper.PaymentStatus toProtobufPaymentStatus(org.restaurant.model.enums.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return StatisticsEventWrapper.PaymentStatus.UNRECOGNIZED;
        }
        switch (paymentStatus) {
            case PAYMENT_SUCCESSFUL:
                return StatisticsEventWrapper.PaymentStatus.PAYMENT_SUCCESSFUL;
            case PAYMENT_FAILED:
                return StatisticsEventWrapper.PaymentStatus.PAYMENT_FAILED;
            case PAYMENT_PROCESSING:
                return StatisticsEventWrapper.PaymentStatus.PAYMENT_PROCESSING;
            default:
                throw new IllegalArgumentException("Unknown PaymentStatus: " + paymentStatus);
        }
    }
    public static StatisticsEventWrapper.DeliveryOption toProtobufDeliveryOption(DeliveryOption deliveryOption) {
        if (deliveryOption == null) {
            return StatisticsEventWrapper.DeliveryOption.UNRECOGNIZED; // Obsługa nulli
        }

        switch (deliveryOption) {
            case PICK_UP:
                return StatisticsEventWrapper.DeliveryOption.PICK_UP;
            case DELIVERY:
                return StatisticsEventWrapper.DeliveryOption.DELIVERY;
            default:
                throw new IllegalArgumentException("Unknown DeliveryOption: " + deliveryOption);
        }
    }

}
