package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.subscription.CheckoutRequest;
import com.project.lovable_clone.dto.subscription.CheckoutResponse;
import com.project.lovable_clone.dto.subscription.PortalResponse;
import com.project.lovable_clone.entity.Plan;
import com.project.lovable_clone.entity.User;
import com.project.lovable_clone.error.PaymentException;
import com.project.lovable_clone.error.ResourceNotFoundException;
import com.project.lovable_clone.repository.PlanRepository;
import com.project.lovable_clone.repository.UserRepository;
import com.project.lovable_clone.security.AuthUtil;
import com.project.lovable_clone.service.PaymentProcessor;

import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripePaymentProcessorImpl implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user", userId.toString()));

        Plan plan = planRepository.findById(checkoutRequest.planId()).orElseThrow(
                () -> new ResourceNotFoundException("Plan", checkoutRequest.planId().toString()));

        var params = SessionCreateParams.builder()
                .addLineItem(SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE).build()).build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id", userId.toString())
                .putMetadata("plan_id", plan.getId().toString());


        if (StringUtils.hasLength(user.getStripeCustomerId())) {
            params.setCustomer(user.getStripeCustomerId());
        } else {
            params.setCustomerEmail(user.getUsername());
        }

        try {
            Session session = Session.create(params.build());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new PaymentException("Failed to create Stripe checkout session", e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        return;
    }
}
