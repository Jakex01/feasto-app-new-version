package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.dto.CustomerDetailsDto;
import org.example.service.CustomerStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticController {
    private final CustomerStatsService customerStatsService;

    @GetMapping("/top-customers/{restaurantId}")
    public ResponseEntity<List<CustomerDetailsDto>> getTopCustomers(@PathVariable Long restaurantId) throws Exception {
        List<CustomerDetailsDto> topCustomers = customerStatsService.getTop5CustomersWithDetails(restaurantId);
        return ResponseEntity.ok(topCustomers);
    }
}
