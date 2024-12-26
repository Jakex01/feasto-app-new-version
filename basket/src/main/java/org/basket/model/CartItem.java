package org.basket.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;


@AllArgsConstructor
@Data
public class CartItem implements Serializable {

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        private String name;
        private String category;
        private String restaurantId;
        private String description;
        private String restaurantName;
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 1000, message = "Quantity cannot exceed 1000")
        private Integer quantity;

        @NotBlank(message = "Size cannot be blank")
        @Pattern(regexp = "^(Small|Medium|Large)$", message = "Size must be Small, Medium, or Large")
        private String size;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private Integer price;

        @NotNull(message = "Additives cannot be null")
        @Size(max = 10, message = "Additives cannot have more than 10 entries")
        private Map<@NotBlank(message = "Additive key cannot be blank") String,
                        @Positive(message = "Additive value must be positive") Double> additives;

}
