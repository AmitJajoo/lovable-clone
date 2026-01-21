package com.project.lovable_clone.controller;

import com.project.lovable_clone.dto.subscription.*;
import com.project.lovable_clone.service.PaymentProcessor;
import com.project.lovable_clone.service.PlanService;
import com.project.lovable_clone.service.SubscriptionService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataDeserializer;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlan() {
        return ResponseEntity.ok(planService.getAllActivePlan());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscription() {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(checkoutRequest));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signHeader
    ) {
        try{
            Event event = Webhook.constructEvent(payload, signHeader, webhookSecret);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if(deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                //Fallback: Deserialize from raw json
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("Failed to deserialize webhook object for event: {}", event.getType());
                        return ResponseEntity.ok().build();
                    }
                }catch (Exception e) {
                    log.error("Unsafe deserialization failed for event {}: {}",event.getType(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }

            //Now extract metadata only if it's checkout session
            Map<String, String> metadata = new HashMap<>();
            if(stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }

            // Pass to your processor
            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
