package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.*;
import org.example.model.enums.DeliveryOption;
import org.example.service.interf.RestaurantStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats/user")
public class RestaurantStatsController {
    private final RestaurantStatsService restaurantStatsService;

    @GetMapping("/{restaurantId}/users")
    public ResponseEntity<List<UserStats>> getTopUsersByRestaurant(
            @PathVariable Long restaurantId,
            @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getTopUsersByRestaurant(restaurantId, token);
    }

    @GetMapping("/{restaurantId}/daily-revenue")
    public ResponseEntity<List<RevenueStats>> getDailyRevenue(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getDailyRevenue(restaurantId, startDate, endDate,  token);
    }

    @GetMapping("/{restaurantId}/daily-order-count")
    public ResponseEntity<List<OrderCountStats>> getDailyOrderCount(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getDailyOrderCount(restaurantId, startDate, endDate, token);
    }

    @GetMapping("/{restaurantId}/daily-finish-time")
    public ResponseEntity<List<FinishTimeStats>> getDailyFinishTime(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getDailyFinishTime(restaurantId, startDate, endDate, token);
    }

    @GetMapping("/{restaurantId}/current-orders")
    public ResponseEntity<Integer> getCurrentOrders(@PathVariable Long restaurantId,
                                                    @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getCurrentOrders(restaurantId, token);
    }

    @GetMapping("/{restaurantId}/top-financial-days")
    public ResponseEntity<List<FinancialDayStats>> getTop3FinancialDays(@PathVariable Long restaurantId,
                                                                        @RequestHeader(value = "Authorization") String token) {
        return restaurantStatsService.getTOp3FinancialDays(restaurantId, token);
    }

    @GetMapping("/{restaurantId}/delivery-options")
    public ResponseEntity<Map<DeliveryOption, Long>> getDeliveryOptionDistribution(@PathVariable Long restaurantId,
                                                                                   @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(restaurantStatsService.getDeliveryOptionDistribution(restaurantId, token));
    }

}
