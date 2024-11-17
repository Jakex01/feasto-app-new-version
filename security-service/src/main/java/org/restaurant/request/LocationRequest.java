package org.restaurant.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocationRequest(
        @NotBlank(message = "City cannot be blank")
        @Size(max = 100, message = "City name must not exceed 100 characters")
        String city,

        @NotBlank(message = "Street cannot be blank")
        @Size(max = 100, message = "Street name must not exceed 100 characters")
        String street,

        @NotBlank(message = "Street number cannot be blank")
        @Pattern(regexp = "^[0-9a-zA-Z/-]+$", message = "Invalid street number format")
        String streetNumber,

        @NotBlank(message = "Country cannot be blank")
        @Size(max = 100, message = "Country name must not exceed 100 characters")
        String country,

        @NotBlank(message = "Postal code cannot be blank")
        @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$", message = "Invalid postal code format (e.g., 12-345)")
        String postalCode,

        boolean current
) {}
