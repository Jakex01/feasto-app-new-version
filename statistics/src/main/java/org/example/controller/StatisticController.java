package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticController {
    private final StatisticService statisticsService;


    @GetMapping("/order-count/{restaurantId}")
    public ResponseEntity<Map<LocalDate, Long>> getOrderCountByDayForRestaurantId(@PathVariable Long restaurantId) {
        return statisticsService.getOrderCountByDayForOrderId(restaurantId);
    }
}
