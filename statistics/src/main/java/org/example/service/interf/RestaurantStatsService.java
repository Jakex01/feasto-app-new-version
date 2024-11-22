package org.example.service.interf;

import org.example.model.entity.*;
import org.example.model.enums.DeliveryOption;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RestaurantStatsService {

    ResponseEntity<List<UserStats>> getTopUsersByRestaurant(Long restaurantId, String token);
    ResponseEntity<List<RevenueStats>> getDailyRevenue(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token);

    ResponseEntity<List<OrderCountStats>> getDailyOrderCount(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token);
    ResponseEntity<List<FinishTimeStats>> getDailyFinishTime(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token);
    ResponseEntity<Integer> getCurrentOrders(Long restaurantId, String token);
    ResponseEntity<List<FinancialDayStats>> getTOp3FinancialDays(Long restaurantId, String token);
     Map<DeliveryOption, Long> getDeliveryOptionDistribution(Long restaurantId, String token);
}
