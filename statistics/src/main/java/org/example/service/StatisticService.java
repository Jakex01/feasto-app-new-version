package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.StatisticNotFoundException;
import org.example.model.entity.Statistics;
import org.example.repository.StatsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatsRepository statisticsRepository;
    private static final String STATISTIC_NOT_FOUND = "No statistics found for restaurantId ";

    public ResponseEntity<Map<LocalDate, Long>> getOrderCountByDayForOrderId(Long restaurantId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(10);

        List<Statistics> statistics = statisticsRepository
                .findByRestaurantIdAndOrderDateBetween(restaurantId, startDate, endDate);
        if (statistics.isEmpty()) {
            throw new StatisticNotFoundException(Set.of(STATISTIC_NOT_FOUND + restaurantId));
        }
        Map<LocalDate, Long> statGrouped = statistics.stream()
                .collect(Collectors.groupingBy(
                        stat -> stat.getOrderDate().toLocalDate(),
                        Collectors.counting()
                ));
        return ResponseEntity.ok(statGrouped);
    }
}
