package org.restaurant.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostMenuItemRequest(
        @NotBlank(message = "Name can't be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Name can only contain letters, numbers, and spaces")
        String name,

        @NotBlank(message = "Description can't be blank")
        @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
        String description,

        boolean available,

        @NotBlank(message = "Food category can't be blank")
        @Size(min = 3, max = 50, message = "Food category must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Food category can only contain letters and spaces")
        String foodCategory,
        @NotEmpty(message = "Food additive prices must not be empty")
        List<FoodAdditivePriceRequest> foodAdditivePrices,

        @NotEmpty(message = "Sizes with prices must not be empty")
        List<@Valid SizesWithPrices> sizesWithPrices
) {
}
