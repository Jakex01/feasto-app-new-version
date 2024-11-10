package org.example.repository;

import org.example.model.entity.Statistics;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends ElasticsearchRepository<Statistics, String> {
    List<Statistics> findByRestaurantIdAndOrderDateBetween(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT o. FROM Order o WHERE o.userId = :userId AND o.orderDate >= :cutoffDate")
    Optional<LocalDateTime> findLastOrderDateByUserIdWithinLast10Days(Long userId, LocalDateTime cutoffDate);
}
