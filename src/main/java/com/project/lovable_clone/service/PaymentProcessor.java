package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.subscription.CheckoutRequest;
import com.project.lovable_clone.dto.subscription.CheckoutResponse;
import com.project.lovable_clone.dto.subscription.PortalResponse;

public interface PaymentProcessor {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest);

    PortalResponse openCustomerPortal();
}
