package org.restaurant.repository;

import org.restaurant.model.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findAllByRestaurantRestaurantIdAndCity(Long RestaurantId, String city);
}
