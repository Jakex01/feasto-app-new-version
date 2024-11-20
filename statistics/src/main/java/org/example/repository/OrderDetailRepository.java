package org.example.repository;

import org.example.model.entity.CustomerStats;
import org.example.model.entity.OrderDetail;
import org.example.model.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("""
            SELECT o.userId AS userId, SUM(o.totalPrice) AS totalSpent
            FROM OrderDetail o
            WHERE o.restaurantId = :restaurantId
            GROUP BY o.userId
            ORDER BY SUM(o.totalPrice) DESC
            """)
    List<CustomerStats> findTop5CustomersByRestaurantId(Long restaurantId);

    List<Statistics> findByRestaurantIdAndOrderDateBetween(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate);

    List<Statistics> findByRestaurantId(Long restaurantId);
}