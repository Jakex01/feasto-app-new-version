package org.restaurant.response;

public record MenuItemIdAndNameResponse (
        Long menuItemId,
        String name
) {
}
