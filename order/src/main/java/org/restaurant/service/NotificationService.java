package org.restaurant.service;

import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.restaurant.producer.OrderProducerService;
import org.restaurant.request.OrderRequest;
import org.shared.NotificationEventOuterClass;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final PdfServiceImpl pdfService;
    private final OrderProducerService orderProducerService;


    @Async
    public void sendFileToNotification(OrderRequest orderRequest, String userEmail)  {
        byte[] pdfContent = pdfService.generatePdf(orderRequest);
        NotificationEventOuterClass.NotificationEvent event = NotificationEventOuterClass
                .NotificationEvent.newBuilder()
                .setEmail(userEmail)
                .setPdfContent(ByteString.copyFrom(pdfContent))
                .build();

        orderProducerService.sendOrder(event);
    }

}
