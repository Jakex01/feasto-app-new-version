package org.restaurant.request;
import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
        @NotBlank(message = "restaurant name can't be blank")
        String name,
        @NotBlank(message = "description can't be blank")
        String description,
        @NotBlank(message = "phone number can't be blank")
        String phoneNumber,
        @NotBlank(message = "opening hours can't be blank")
        String openingHours,
        @NotBlank(message = "food type can't be blank")
        String foodType,
        String image
) {
}
