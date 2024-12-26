package org.restaurant.response;

public record LocationResponse(
        Long id,
        String city,
        String street,
        String streetNumber,
        String country,
        String locationName,
        String postalCode,
        boolean current
) {
}
