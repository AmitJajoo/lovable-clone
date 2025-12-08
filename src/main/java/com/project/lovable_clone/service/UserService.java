package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(Long userId);
}
