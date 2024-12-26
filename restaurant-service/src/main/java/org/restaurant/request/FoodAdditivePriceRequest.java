package org.restaurant.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FoodAdditivePriceRequest(
        @NotBlank(message = "Food additive name can't be blank")
        String foodAdditive,

        @NotNull(message = "Price can't be null")
        @Positive(message = "Price must be positive")
        Double price
) {}

