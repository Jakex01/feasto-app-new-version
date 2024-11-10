package org.restaurant.scheduler;

import lombok.RequiredArgsConstructor;
import org.restaurant.service.RestaurantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledRestaurantUpdateTask {

    private final RestaurantService restaurantService;


    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 godzina
    public void updateRestaurantRatings() {
        restaurantService.updateAverageRatingsAndPricing();
    }
}
