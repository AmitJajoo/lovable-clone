package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.subscription.CheckoutRequest;
import com.project.lovable_clone.dto.subscription.CheckoutResponse;
import com.project.lovable_clone.dto.subscription.PortalResponse;
import com.project.lovable_clone.service.PaymentProcessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProcessorImpl implements PaymentProcessor {
    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }
}
