package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.model.entity.*;
import org.example.model.enums.DeliveryOption;
import org.example.model.enums.OrderStatus;
import org.example.repository.OrderDetailRepository;
import org.example.service.interf.RestaurantStatsService;
import org.example.util.JwtUtil;
import org.example.util.UserDetailsClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RestaurantStatsServiceImpl implements RestaurantStatsService {

    private final OrderDetailRepository orderDetailRepository;
    private final UserDetailsClient detailsClient;
    private static final String USER_URL = "http://localhost:8762/api/user/";

    @Override
    public ResponseEntity<List<UserStats>> getTopUsersByRestaurant(Long restaurantId, String token) {
        String jwtToken = JwtUtil.extractToken(token);
        List<UserStats> top5Customers = orderDetailRepository
                .findTop5CustomersByRestaurantId(restaurantId)
                .stream()
                .peek(userStats -> {
                    String userEmail =  detailsClient.fetchData(USER_URL, String.class, jwtToken, userStats.getUserId()).block();
                    userStats.setUserEmail(userEmail);
                })
                .toList();
        return ResponseEntity.ok(top5Customers);
    }

    @Override
    public ResponseEntity<List<RevenueStats>> getDailyRevenue(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
        List<RevenueStats> revenueStats = orderDetailRepository.getDailyRevenueByRestaurantAndDateRange(restaurantId, startDate, endDate);
        return ResponseEntity.ok(revenueStats);
    }

    @Override
    public ResponseEntity<List<OrderCountStats>> getDailyOrderCount(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
        List<OrderCountStats> orderCountStats = orderDetailRepository.getDailyOrdersCountByRestaurantAndDateRange(restaurantId, startDate, endDate);
        return ResponseEntity.ok(orderCountStats);
    }

    @Override
    public ResponseEntity<List<FinishTimeStats>> getDailyFinishTime(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
       List<FinishTimeStats> finishTimeStats = orderDetailRepository.getDailyAvgFinishOrderTimeByRestaurantAndDateRange(restaurantId, startDate, endDate);
        return ResponseEntity.ok(finishTimeStats);
    }

    @Override
    public ResponseEntity<Integer> getCurrentOrders(Long restaurantId, String token) {
        Integer count = orderDetailRepository.findByRestaurantIdAAndOrderStatus(restaurantId, OrderStatus.PENDING).size();
        return ResponseEntity.ok(count);
    }

    @Override
    public ResponseEntity<List<FinancialDayStats>> getTOp3FinancialDays(Long restaurantId, String token) {
        List<FinancialDayStats> financialDayStats = orderDetailRepository.findTop3FinancialDaysByRestaurantId(restaurantId);
        return ResponseEntity.ok(financialDayStats);
    }

    @Override
    public Map<DeliveryOption, Long> getDeliveryOptionDistribution(Long restaurantId, String token) {
        List<Object[]> results = orderDetailRepository.getDeliveryOptionDistributionByRestaurantId(restaurantId);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (DeliveryOption) result[0],
                        result -> (Long) result[1]
                ));
    }
}
