package org.payments.config.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.payments.request.PaymentRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class PaypalController {
    private final PaypalService paypalService;

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestBody PaymentRequest paymentRequest
            ){

        try{

            String cancelUrl = "https://localhost:8081/payment/cancel";
            String successUrl = "https://localhost:8081/payment/success";

            Payment payment = paypalService.createPayment(
                    paymentRequest.amount(),
                    "PLN",
                    "paypal",
                    "sale",
                    "payment",
                    cancelUrl,
                    successUrl
            );

            for (Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                 return new RedirectView(links.getHref());
                }
            }

        }catch (PayPalRESTException e){
            log.error("Error occurred: ",e);
        }
        return new RedirectView("/payment/error");
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ){
        System.out.println("success 1");
        try{
            System.out.println("success 2");
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                return "paymentSuccess";
            }


        }catch (PayPalRESTException e){
            log.error("Error occurred: ",e);
        }
        return "paymentSuccess";
    }
    @GetMapping("/payment/cancel")
    public String paymentCancel(){
        return "paymentCancel";
    }
    @GetMapping("/payment/error")
    public String paymentError(){
        return "paymentError";
    }


}
