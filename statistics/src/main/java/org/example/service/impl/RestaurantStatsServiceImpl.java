package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.model.entity.*;
import org.example.model.enums.DeliveryOption;
import org.example.model.enums.OrderStatus;
import org.example.repository.OrderDetailRepository;
import org.example.service.interf.RestaurantStatsService;
import org.example.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RestaurantStatsServiceImpl implements RestaurantStatsService {

    private final OrderDetailRepository orderDetailRepository;
//    private final UserDetailsClient detailsClient;
    private static final String USER_URL = "http://api-gateway:8762/api/security/user";

    @Override
    public ResponseEntity<List<UserStats>> getTopUsersByRestaurant(Long restaurantId, String token) {
        String jwtToken = JwtUtil.extractToken(token);
        List<UserStats> top5Customers = orderDetailRepository
                .findTop5CustomersByRestaurantId(restaurantId)
                .stream()
                .peek(userStats -> {
//                    String userEmail =  detailsClient.fetchData(USER_URL, String.class, jwtToken, userStats.getUserId()).block();
                    String userEmail = "gowno";
                    userStats.setUserEmail(userEmail);
                })
                .toList();
        return ResponseEntity.ok(top5Customers);
    }

    @Override
    public ResponseEntity<List<RevenueStats>> getDailyRevenue(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
        Map<LocalDate, Double> groupedRevenue = orderDetailRepository
                .getOrdersByRestaurantAndDateRange(restaurantId, startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                        orderDetail -> orderDetail.getCreateDate().toLocalDate(),
                        Collectors.summingDouble(OrderDetail::getTotalPrice)
                ));

        List<RevenueStats> revenueStats = groupedRevenue.entrySet().stream()
                .map(entry -> new RevenueStats(entry.getKey().atStartOfDay(), entry.getValue()))
                .sorted(Comparator.comparing(RevenueStats::getDate))
                .toList();
        return ResponseEntity.ok(revenueStats);
    }

    @Override
    public ResponseEntity<List<OrderCountStats>> getDailyOrderCount(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
        List<OrderDetail> orderDetails = orderDetailRepository.getOrdersByRestaurantAndDateRange(restaurantId, startDate, endDate);
        Map<LocalDate, Long> groupedOrderCounts = orderDetails.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreateDate().toLocalDate(),
                        Collectors.counting()
                ));

        List<OrderCountStats> orderCountStats = groupedOrderCounts.entrySet().stream()
                .map(entry -> new OrderCountStats(entry.getKey(), entry.getValue().intValue()))
                .sorted(Comparator.comparing(OrderCountStats::getDate))
                .toList();
        return ResponseEntity.ok(orderCountStats);
    }


    @Override
    public ResponseEntity<List<FinishTimeStats>> getDailyFinishTime(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, String token) {
        List<OrderDetail> orderDetails = orderDetailRepository.getOrdersByRestaurantAndDateRange(restaurantId, startDate, endDate);

        Map<LocalDate, Double> groupedFinishTimes = orderDetails.stream()
                .filter(order -> order.getFinishedDate() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getCreateDate().toLocalDate(),
                        Collectors.averagingDouble(order ->
                                order.getFinishedDate().until(order.getCreateDate(), ChronoUnit.MINUTES) * -1
                        )
                ));

        List<FinishTimeStats> finishTimeStats = groupedFinishTimes.entrySet().stream()
                .map(entry -> new FinishTimeStats(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(FinishTimeStats::getDate))
                .toList();
        return ResponseEntity.ok(finishTimeStats);
    }

    @Override
    public ResponseEntity<Integer> getCurrentOrders(Long restaurantId, String token) {
        Integer count = orderDetailRepository.findByRestaurantIdAndOrderStatus(restaurantId, OrderStatus.PENDING).size();
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
