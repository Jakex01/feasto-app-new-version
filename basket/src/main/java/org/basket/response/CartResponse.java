package org.basket.response;

import jakarta.validation.constraints.*;
import java.util.Map;

public record CartResponse(
        Long menuItemId, // Include menuItemId here

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        String category,

        String restaurantId,

        String restaurantName,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 1000, message = "Quantity cannot exceed 1000")
        Integer quantity,

        @NotBlank(message = "Size cannot be blank")
        @Pattern(regexp = "^(Small|Medium|Large)$", message = "Size must be Small, Medium, or Large")
        String size,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Integer price,

        @NotNull(message = "Additives cannot be null")
        @Size(max = 10, message = "Additives cannot have more than 10 entries")
        Map<@NotBlank(message = "Additive key cannot be blank") String,
                @Positive(message = "Additive value must be positive") Double> additives
) {}
