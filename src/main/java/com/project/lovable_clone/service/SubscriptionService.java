package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.subscription.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionResponse> getCurrentSubscription();
}
