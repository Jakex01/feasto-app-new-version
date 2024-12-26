package org.restaurant.response;

import org.restaurant.model.enums.DeliveryOption;
import org.restaurant.model.enums.OrderStatus;
import org.restaurant.model.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Double totalPrice,
        OrderStatus orderStatus,
        int expectedDeliveryTimeInMinutes,
        double deliveryFee,
        String restaurantName,
        Long restaurantId,
        DeliveryOption deliveryOption,
        String orderNote,
        PaymentStatus paymentStatus,
        LocalDateTime createDate,
        List<MenuItemResponse> items

) {
}
