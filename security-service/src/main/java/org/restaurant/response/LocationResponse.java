package org.restaurant.response;

public record LocationResponse(
        Long id,
        String city,
        String street,
        String streetNumber,
        String country,
        String postalCode,
        boolean current
) {
}
