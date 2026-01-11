package com.project.lovable_clone.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ProjectRole {
    EDITOR(Set.of(ProjectPermission.EDIT, ProjectPermission.VIEW, ProjectPermission.DELETE)),
    VIEWER(Set.of(ProjectPermission.VIEW)),
    OWNER(Set.of(ProjectPermission.VIEW, ProjectPermission.EDIT, ProjectPermission.DELETE, ProjectPermission.MANAGE_MEMBERS));

    private final Set<ProjectPermission> permissions;
}
