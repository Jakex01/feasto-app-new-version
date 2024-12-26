package org.example.util;

//@Service
//@AllArgsConstructor
//public class UserOrderService {
//
//    private final OrderDetailRepository orderDetailRepository;
////    private final EmailProducerService emailProducerService;
////    public void checkUsersOrders(List<String> usersEvent) {
////        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(20);
////        List<EmailReminderEventWrapper.EmailReminderEvent> emailReminderEvents = new ArrayList<>();
////        for(String userEvent : usersEvent) {
////            Optional<LocalDateTime> lastOrder = orderDetailRepository.findLastOrderDateByUserIdWithinLast10Days(userEvent, cutoffDate);
////            if(lastOrder.isPresent()) {
////                EmailReminderEventWrapper.EmailReminderEvent event = EmailReminderEventWrapper
////                        .EmailReminderEvent.newBuilder()
////                        .setUserEmail(userEvent)
////                        .setCreateDate(toProtobufTimestamp(lastOrder.get()))
////                        .build();
////                emailReminderEvents.add(event);
////            }
////        }
//////        emailProducerService.sendEmailReminder(emailReminderEvents);
////
////    }
////    public static Timestamp toProtobufTimestamp(LocalDateTime localDateTime) {
////        if (localDateTime == null) {
////            return Timestamp.getDefaultInstance();
////        }
////        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
////        return Timestamp.newBuilder()
////                .setSeconds(instant.getEpochSecond())
////                .setNanos(instant.getNano())
////                .build();
////    }
//}
