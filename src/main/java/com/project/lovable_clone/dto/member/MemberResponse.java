package com.project.lovable_clone.dto.member;

import com.project.lovable_clone.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(Long userId, String email, String name,
                             String avatarUrl, ProjectRole role,
                             Instant invitedAt) {
}
