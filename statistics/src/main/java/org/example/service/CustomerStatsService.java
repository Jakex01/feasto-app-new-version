package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.dto.CustomerDetailsDto;
import org.example.model.entity.CustomerStats;
import org.example.util.CustomerDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerStatsService {

    private final StatisticService statisticService;
    private final CustomerDetailsService customerDetailsService;

    public List<CustomerDetailsDto> getTop5CustomersWithDetails(Long restaurantId) throws Exception {
        List<CustomerStats> customerStats = statisticService.getTop5CustomersByRestaurantId(restaurantId);

        return customerStats.stream()
                .map(stats -> {
                    CustomerDetailsDto details = customerDetailsService.getCustomerDetails(stats.getUserId());
                    details.setTotalSpent(stats.getTotalSpent());
                    return details;
                })
                .toList();
    }

}
