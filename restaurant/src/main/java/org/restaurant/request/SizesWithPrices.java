package org.restaurant.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SizesWithPrices(
        @NotBlank(message = "Size can't be blank")
        @Size(min = 1, max = 50, message = "Size must be between 1 and 50 characters")
        String size,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        double price
) {
}
