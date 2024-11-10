package org.restaurant.request;

public record FilterRestaurant(
        String restaurantName,
        String foodType,
        Double rating,
        Double priceRange,
        String sort
) {

}
