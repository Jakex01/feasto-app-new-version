package org.restaurant.request;

import jakarta.validation.constraints.NotBlank;

public record PostLocationRequest(
        @NotBlank(message = "City can't be blank")
         String city,
         @NotBlank(message = "Street can't be blank")
         String street,
         @NotBlank(message = "Street number can't be blank")
         String streetNumber,
         @NotBlank(message = "Country can't be blank")
         String country,
        String postalCode,

        boolean current
) {
}
