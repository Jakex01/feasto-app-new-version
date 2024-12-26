package org.restaurant.request.update;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;


public record UpdateRestaurantRequest(
        @Nullable
        @Length(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @Length(max = 255, message = "Description cannot exceed 255 characters")
        @Nullable
        String description,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
        @Nullable
        String phoneNumber,

        @Nullable
        @Pattern(
                regexp = "^(0[1-9]|1[0-2]) (AM|PM) - (0[1-9]|1[0-2]) (AM|PM)$",
                message = "Opening hours must be in format HH AM/PM - HH AM/PM"
        )
        String openingHours,

        @Nullable
        @Length(max = 50, message = "Food type cannot exceed 50 characters")
        String foodType,

        @Pattern(
                regexp = "^(http|https)://.*\\.(jpeg|jpg|png)$",
                message = "Image must be a valid URL to a JPEG or PNG image"
        )
        @Nullable
        String image

) {
}
