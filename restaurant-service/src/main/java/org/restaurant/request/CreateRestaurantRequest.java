package org.restaurant.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(
        @NotBlank(message = "Restaurant name can't be blank")
        @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Restaurant name can only contain letters and spaces")
        String name,

        @NotBlank(message = "Description can't be blank")
        @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
        String description,

        @NotBlank(message = "Phone number can't be blank")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone number must be valid (e.g., +123456789 or 123456789)")
        String phoneNumber,

        @NotBlank(message = "Opening hours can't be blank")
        @Size(min = 5, max = 50, message = "Opening hours must be between 5 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s:\\-,]+$", message = "Opening hours can only contain letters, numbers, and common time-related symbols")
        String openingHours,

        @NotBlank(message = "Food type can't be blank")
        @Size(min = 3, max = 50, message = "Food type must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Food type can only contain letters and spaces")
        String foodType
) {
}
