package org.restaurant.model.event;



public record NotificationEvent(
         String email,
         String pdfContent
) {
}
