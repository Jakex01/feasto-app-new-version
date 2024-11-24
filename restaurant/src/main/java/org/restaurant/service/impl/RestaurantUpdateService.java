package org.restaurant.service.impl;

import org.restaurant.model.RestaurantEntity;
import org.springframework.stereotype.Service;

import static org.restaurant.util.RestaurantUtils.*;

@Service
public class RestaurantUpdateService {

    public void updateRatingsAndPrices(RestaurantEntity restaurant) {
        double newRating = calculateAverageRating(restaurant.getRatings());
        int newPrice = (int) calculateAveragePrice(restaurant);
        long newCommentsCount = calculateCountOfRatings(restaurant.getRatings());

        restaurant.setRating(newRating);
        restaurant.setPrices(newPrice);
        restaurant.setCommentsCount(newCommentsCount);
    }

}

