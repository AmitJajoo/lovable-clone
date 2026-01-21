package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.subscription.SubscriptionResponse;
import com.project.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public List<SubscriptionResponse> getCurrentSubscription() {
        return List.of();
    }
}
