package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.auth.AuthResponse;
import com.project.lovable_clone.dto.auth.LoginRequest;
import com.project.lovable_clone.dto.auth.SignUpRequest;
import com.project.lovable_clone.entity.User;
import com.project.lovable_clone.error.BadRequestException;
import com.project.lovable_clone.mapper.UserMapper;
import com.project.lovable_clone.repository.UserRepository;
import com.project.lovable_clone.security.AuthUtil;
import com.project.lovable_clone.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    AuthUtil authUtil;

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        userRepository.findByUsername(signUpRequest.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username: " + signUpRequest.username());
        });

        User user = userMapper.toEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user = userRepository.save(user);
        return new AuthResponse(authUtil.generateAccessToken(user), userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
