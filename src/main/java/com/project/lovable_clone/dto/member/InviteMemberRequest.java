package com.project.lovable_clone.dto.member;

import com.project.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
       @NotBlank @Email String email,
       @NotNull ProjectRole role) {
}
