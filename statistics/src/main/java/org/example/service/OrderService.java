package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.CustomerStats;
import org.example.model.entity.DailyEarnings;
import org.example.model.entity.Statistics;
import org.example.model.enums.DeliveryOption;
import org.example.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {


    private final OrderDetailRepository orderDetailRepository;


    public List<CustomerStats> getTop5CustomersByRestaurantId(Long restaurantId) {
        return orderDetailRepository
                .findTop5CustomersByRestaurantId(restaurantId)
                .stream()
                .limit(5)
                .toList();
    }

    public List<DailyEarnings> getEarningsForLastDays(Long restaurantId, int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atStartOfDay();

        List<Statistics> statisticsList = orderDetailRepository.findByRestaurantIdAndOrderDateBetween(
                restaurantId, startDate, endDate);

        return statisticsList.stream()
                .collect(Collectors.groupingBy(
                        stat -> stat.getOrderDate().toLocalDate(),
                        Collectors.summingDouble(Statistics::getTotalPrice)
                ))
                .entrySet()
                .stream()
                .map(entry -> new DailyEarnings(entry.getKey(), entry.getValue()))
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .toList();
    }

    public Map<DeliveryOption, Long> getDeliveryOptionDistribution(Long restaurantId) {
        List<Statistics> statisticsList = orderDetailRepository.findByRestaurantId(restaurantId);
        return statisticsList.stream()
                .collect(Collectors.groupingBy(
                        Statistics::getDeliveryOption,
                        Collectors.counting()
                ));
    }


}
