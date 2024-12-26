package org.restaurant.repository;

import org.restaurant.model.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long>{
    List<RatingEntity> findAllByRestaurantRestaurantId(Long restaurantId);
    Long countByRestaurantRestaurantId(Long restaurantId);

    List<RatingEntity> findAllByUserEmail(String userEmail);

}
