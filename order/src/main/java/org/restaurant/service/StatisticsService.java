package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.model.event.StatisticsEvent;
import org.restaurant.producer.OrderProducerService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderProducerService orderProducerService;
    @Async
    public void sendOrderToStatistics(StatisticsEvent statisticsEvent) {
        orderProducerService.sendStatisticsEvent(statisticsEvent);
    }
}
