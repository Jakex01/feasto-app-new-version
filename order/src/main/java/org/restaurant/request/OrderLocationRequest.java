package org.restaurant.request;

public record OrderLocationRequest(
        String city,
        String street,
        String streetNumber,
        String country,
        String postalCode
) {
}