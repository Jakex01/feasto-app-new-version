package org.restaurant.response;

import org.restaurant.model.enums.OrderStatus;

import java.util.List;

public record OrderResponse(
        Long id,
        Double totalPrice,
        OrderStatus orderStatus,
        List<MenuItemResponse> menuItemResponseList

) {
}
