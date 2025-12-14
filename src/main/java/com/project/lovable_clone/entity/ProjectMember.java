package com.project.lovable_clone.entity;

import com.project.lovable_clone.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "project_members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMember {
    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId")
    Project project;
    @ManyToOne
    @MapsId("userId")
    User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectRole role;
    Instant invitedAt;
    Instant acceptedAt;
}
