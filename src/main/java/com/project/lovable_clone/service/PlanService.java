package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.subscription.PlanResponse;

import java.util.List;

public interface PlanService {
     List<PlanResponse> getAllActivePlan();
}
