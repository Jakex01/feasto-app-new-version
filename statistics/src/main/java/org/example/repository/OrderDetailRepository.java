package org.example.repository;

import org.example.model.entity.*;
import org.example.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("""
        SELECT
            o.userEmail AS userId,
            COUNT(o.id) AS ordersCount,
            MAX(o.createDate) AS lastOrder,
            SUM(o.totalPrice) AS totalSpent
        FROM OrderDetail o
        WHERE o.restaurantId = :restaurantId
        GROUP BY o.userEmail
        ORDER BY SUM(o.totalPrice) DESC
        LIMIT 5
        """)
    List<UserStats> findTop5CustomersByRestaurantId(Long restaurantId);

    @Query("""
        SELECT o
        FROM OrderDetail o
        WHERE o.restaurantId = :restaurantId
        AND o.createDate BETWEEN :startDate AND :endDate
        ORDER BY o.createDate ASC
        """)
    List<OrderDetail> getOrdersByRestaurantAndDateRange(@Param("restaurantId") Long restaurantId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.createDate) AS day, COUNT(o.id) AS ordersCount " +
            "FROM OrderDetail o " +
            "WHERE o.restaurantId = :restaurantId " + // Added condition for restaurantId
            "AND o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createDate) " +
            "ORDER BY DATE(o.createDate) ASC")
    List<OrderCountStats> getDailyOrdersCountByRestaurantAndDateRange(@Param("restaurantId") Long restaurantId,
                                                                      @Param("startDate") LocalDateTime startDate,
                                                                      @Param("endDate") LocalDateTime endDate);


    @Query(value = "SELECT DATE(order_date) AS day, " +
            "AVG(EXTRACT(EPOCH FROM (finished_date - order_date)) / 60) AS average_finish_time " +
            "FROM OrderDetail " +
            "WHERE restaurantId = :restaurantId " +
            "AND order_date BETWEEN :startDate AND :endDate " +
            "AND finished_date IS NOT NULL " +
            "GROUP BY DATE(order_date) " +
            "ORDER BY DATE(order_date) ASC",
            nativeQuery = true)
    List<FinishTimeStats> getDailyAvgFinishOrderTimeByRestaurantAndDateRange(@Param("restaurantId") Long restaurantId,
                                                                             @Param("startDate") LocalDateTime startDate,
                                                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.createDate) AS day, SUM(o.totalPrice) AS totalRevenue " +
            "FROM OrderDetail o " +
            "WHERE o.restaurantId = :restaurantId " +
            "GROUP BY DATE(o.createDate) " +
            "ORDER BY totalRevenue DESC " +
            "LIMIT 3")
    List<FinancialDayStats> findTop3FinancialDaysByRestaurantId(@Param("restaurantId") Long restaurantId);

    List<OrderDetail> findByRestaurantIdAndOrderStatus(Long restaurantId, OrderStatus orderStatus);
    @Query("SELECT o.deliveryOption, COUNT(o) " +
            "FROM OrderDetail o " +
            "WHERE o.restaurantId = :restaurantId " +
            "GROUP BY o.deliveryOption")
    List<Object[]> getDeliveryOptionDistributionByRestaurantId(@Param("restaurantId") Long restaurantId);
    @Query("SELECT MAX(o.createDate) " +
            "FROM OrderDetail o " +
            "WHERE o.userEmail = :userEmail AND o.createDate > :cutoffDate")
    Optional<LocalDateTime> findLastOrderDateByUserIdWithinLast10Days(@Param("userEmail") String userEmail, @Param("cutoffDate") LocalDateTime cutoffDate);

}