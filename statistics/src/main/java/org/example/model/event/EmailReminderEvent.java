package org.example.model.event;

import java.time.LocalDateTime;

public record EmailReminderEvent (
        String email,
        LocalDateTime lastOrder
){
}
