package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.event.EmailReminderEvent;
import org.example.model.event.UserEvent;
import org.example.producer.EmailProducerService;
import org.example.repository.StatsRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserOrderService {

    private final StatsRepository statsRepository;
    private final EmailProducerService emailProducerService;
    public void CheckUsersOrders(List<UserEvent> usersEvent) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(10);
        List<EmailReminderEvent> emailReminderEvents = new ArrayList<>();
        for(UserEvent userEvent : usersEvent) {
            Optional<LocalDateTime> lastOrder = statsRepository.findLastOrderDateByUserIdWithinLast10Days(userEvent.userId(), cutoffDate);
            if(lastOrder.isPresent()) {
                EmailReminderEvent event = new EmailReminderEvent(userEvent.email(), lastOrder.get());
                emailReminderEvents.add(event);
            }
        }
        emailProducerService.sendEmailReminder(emailReminderEvents);

    }
}
