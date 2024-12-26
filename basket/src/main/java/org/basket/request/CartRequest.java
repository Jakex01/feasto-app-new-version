package org.basket.request;

import jakarta.validation.constraints.NotNull;

public record CartRequest(
        @NotNull(message = "MenuItemId cannot be null")
        Long menuItemId,

        @NotNull(message = "CartItem cannot be null")
        CartMenuItemRequest cartItem
) {
}
