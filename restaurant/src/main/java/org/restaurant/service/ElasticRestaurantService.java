package org.restaurant.service;

import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.request.FilterRestaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ElasticRestaurantService {

    void saveRestaurant(RestaurantEntity restaurantEntity);
    Page<ElasticRestaurant> filterRestaurants(FilterRestaurant filterRestaurant, Pageable pageable) throws RestaurantSearchException;

    void deleteById(String restaurantId);
}
