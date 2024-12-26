package org.payments.controller;

import lombok.AllArgsConstructor;
import org.payments.model.PaymentEvent;
import org.payments.model.StripeResponse;
import org.payments.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payment")
public class ProductCheckoutController {


    private final StripeService stripeService;


    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody PaymentEvent productRequest) {
        StripeResponse stripeResponse = stripeService.checkProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
}