package org.payments.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.payments.model.PaymentEvent;
import org.payments.model.StripeResponse;
import org.payments.producer.PaymentProducerService;
import org.shared.PaymentEventWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;
    private final PaymentProducerService paymentProducerService;

    public StripeResponse checkProducts(PaymentEvent paymentEvent) {
        Stripe.apiKey= secretKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(paymentEvent.restaurantName()).build();

        SessionCreateParams.LineItem.PriceData unitAmount = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(paymentEvent.currency() == null ? "USD" : paymentEvent.currency())
                .setProductData(productData)
                .setUnitAmount(paymentEvent.amount().longValue()).build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setPriceData(unitAmount)
                        .setQuantity(3L)
                                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment-success")
                .setCancelUrl("http://localhost:4200/payment-failure")
                .addLineItem(lineItem)
                .build();

        Session session = null;
        try {
           session = Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
       sendPaymentToKafka(paymentEvent.orderId(), paymentEvent.userEmail());
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    void sendPaymentToKafka(Long orderId, String userEmail) {
        PaymentEventWrapper.PaymentEvent paymentEvent= PaymentEventWrapper
                .PaymentEvent.newBuilder()
                .setOrderId(orderId)
                .setUserEmail(userEmail)
                .setPaymentStatus(PaymentEventWrapper.PaymentEvent.PaymentStatus.PAYMENT_SUCCESSFUL)
                .build();
        paymentProducerService.sendOrder(paymentEvent);
    }

}
