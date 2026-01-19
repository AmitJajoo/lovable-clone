package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.subscription.CheckoutRequest;
import com.project.lovable_clone.dto.subscription.CheckoutResponse;
import com.project.lovable_clone.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
