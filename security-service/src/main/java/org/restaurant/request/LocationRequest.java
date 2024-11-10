package org.restaurant.request;

public record LocationRequest(
         String city,
         String street,
         String streetNumber,
         String country,
         String postalCode,
         boolean current
) {
}
