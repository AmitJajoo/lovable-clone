package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.member.InviteMemberRequest;
import com.project.lovable_clone.dto.member.MemberResponse;
import com.project.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.project.lovable_clone.entity.ProjectMember;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest);

    void removeProjectMember(Long projectId, Long memberId);
}
