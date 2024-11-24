package org.restaurant.scheduler;

import lombok.RequiredArgsConstructor;
import org.restaurant.service.impl.RestaurantServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledRestaurantUpdateTask {

    private final RestaurantServiceImpl restaurantServiceImpl;


    @Scheduled(fixedRate = 3600000)
    private void updateRestaurantRatings() {
        restaurantServiceImpl.updateAverageRatingsAndPricing();
    }
}
