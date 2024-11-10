package org.restaurant.response;

public record LocationResponse(
        String city,
        String street,
        String streetNumber,
        String country
) {
}
