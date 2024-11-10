package org.restaurant.util;

import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.RatingEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.SizesWithPricesEntity;
import org.restaurant.request.SizesWithPrices;

import java.util.List;

public class RestaurantUtils {

    public static double calculateAverageRating(List<RatingEntity> ratings) {
        return ratings.stream()
                .mapToDouble(RatingEntity::getRating)
                .average()
                .orElse(4.0);
    }
    public static double calculateAveragePrice(RestaurantEntity restaurant) {
//        return restaurant
//                .getMenuItems()
//                .stream()
//                .map(MenuItemEntity::getSizesWithPrices)
//                .map(SizesWithPricesEntity::getPrice)
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElse(0.0);
        return 5.0;
    }
    public static long calculateCountOfRatings(List<RatingEntity> ratings) {
        return ratings.size();
    }
}
