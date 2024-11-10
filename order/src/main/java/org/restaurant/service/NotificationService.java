package org.restaurant.service;

import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.restaurant.model.event.NotificationEvent;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.request.OrderRequest;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.shared.NotificationEventOuterClass;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebClientService webClientService;
    private final PdfServiceImpl pdfService;
    private final OrderProducerService orderProducerService;
    private static final String USER_URL = "http://localhost:8762/api/auth/user";


    @Async
    public void SendFileToNotification(OrderRequest orderRequest, String token)  {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        byte[] pdfContent = pdfService.generatePdf(orderRequest);
        NotificationEvent notificationEvent = new NotificationEvent(userEmail, Base64.getEncoder().encodeToString(pdfContent));
        NotificationEventOuterClass.NotificationEvent event = NotificationEventOuterClass
                .NotificationEvent.newBuilder()
                .setEmail(userEmail)
                .setPdfContent(ByteString.copyFrom(pdfContent))
                .build();

        orderProducerService.sendOrder(event);
    }

}
