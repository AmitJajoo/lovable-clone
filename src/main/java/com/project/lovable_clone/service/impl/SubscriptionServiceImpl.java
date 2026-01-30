package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.subscription.SubscriptionResponse;
import com.project.lovable_clone.security.AuthUtil;
import com.project.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;

    @Override
    public List<SubscriptionResponse> getCurrentSubscription() {
        return List.of();
    }

    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        return false;//TODO: complete it
    }
}
