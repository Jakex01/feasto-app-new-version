package org.restaurant.repository;

import org.restaurant.model.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findAllByRestaurantRestaurantIdAndCity(Long RestaurantId, String city);
    List<LocationEntity> findAllByRestaurantRestaurantId(Long restaurantId);
}
