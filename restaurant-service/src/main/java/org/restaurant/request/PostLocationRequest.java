package org.restaurant.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostLocationRequest(
        @NotBlank(message = "City can't be blank")
        @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "City name can only contain letters and spaces")
        String city,

        @NotBlank(message = "Street can't be blank")
        @Size(min = 2, max = 100, message = "Street name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Street name can only contain letters, numbers, and spaces")
        String street,

        @NotBlank(message = "Street number can't be blank")
        @Size(min = 1, max = 10, message = "Street number must be between 1 and 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Street number can only contain letters, numbers, and dashes")
        String streetNumber,

        @NotBlank(message = "Country can't be blank")
        @Size(min = 2, max = 50, message = "Country name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Country name can only contain letters and spaces")
        String country,

        @Size(max = 10, message = "Postal code must be up to 10 characters")
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-]+$", message = "Postal code can only contain letters, numbers, spaces, and dashes")
        String postalCode,

        boolean current
) {
}
