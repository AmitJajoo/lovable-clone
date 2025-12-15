package com.project.lovable_clone.controller;

import com.project.lovable_clone.dto.member.InviteMemberRequest;
import com.project.lovable_clone.dto.member.MemberResponse;
import com.project.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.project.lovable_clone.entity.ProjectMember;
import com.project.lovable_clone.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
@RestController
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMember(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(@PathVariable Long projectId,
                                                       @RequestBody @Valid InviteMemberRequest inviteMemberRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                projectMemberService.inviteMember(projectId, inviteMemberRequest)
        );
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId,
                                                           @PathVariable Long memberId,
                                                           @RequestBody @Valid UpdateMemberRoleRequest updateMemberRoleRequest) {
        return ResponseEntity.ok(
                projectMemberService.updateMemberRole(projectId, memberId, updateMemberRoleRequest)
        );
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMemberRole(@PathVariable Long projectId,
                                                           @PathVariable Long memberId) {
        projectMemberService.removeProjectMember(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

}
