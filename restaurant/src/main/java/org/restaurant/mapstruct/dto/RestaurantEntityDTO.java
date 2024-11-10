package org.restaurant.mapstruct.dto;

import lombok.Data;

@Data
public class RestaurantEntityDTO {

    private Long restaurantId;
    private String name;
    private String description;
    private String openingHours;
    private String image;
    private double rating;
    private int prices;

}
