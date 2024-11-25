package org.restaurant.service;

import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.UserNotValidException;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.request.OrderRequest;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.WebClientService;
import org.shared.NotificationEventOuterClass;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebClientService webClientService;
    private final PdfServiceImpl pdfService;
    private final OrderProducerService orderProducerService;
    private static final String USER_URL = "http://localhost:8762/api/auth/user";


    @Async
    public void sendFileToNotification(OrderRequest orderRequest, String token)  {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null) {
            throw new UserNotValidException("User not present");
        }
        byte[] pdfContent = pdfService.generatePdf(orderRequest);
        NotificationEventOuterClass.NotificationEvent event = NotificationEventOuterClass
                .NotificationEvent.newBuilder()
                .setEmail(userEmail)
                .setPdfContent(ByteString.copyFrom(pdfContent))
                .build();

        orderProducerService.sendOrder(event);
    }

}
