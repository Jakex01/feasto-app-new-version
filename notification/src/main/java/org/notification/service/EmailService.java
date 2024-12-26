package org.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.shared.NotificationEventOuterClass;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;


    @Async
    @SneakyThrows
    public void sendEmailWithPdf(NotificationEventOuterClass.NotificationEvent orderRequest) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(orderRequest.getEmail());
        helper.setTo(orderRequest.getEmail());
        helper.setSubject("Your bill");
        helper.setText("Find attached your invoice.");

        ByteArrayDataSource dataSource = new ByteArrayDataSource(orderRequest.getPdfContent().toByteArray(), "application/pdf");
        helper.addAttachment("bill.pdf", dataSource);

        emailSender.send(message);
    }
}
