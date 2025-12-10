package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.auth.UserProfileResponse;
import com.project.lovable_clone.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        return null;
    }
}
