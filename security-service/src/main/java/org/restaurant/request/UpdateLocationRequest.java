package org.restaurant.request;

public record UpdateLocationRequest(
        Long id,
        boolean current
) {
}
