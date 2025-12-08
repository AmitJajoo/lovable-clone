package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.project.ProjectRequest;
import com.project.lovable_clone.dto.project.ProjectResponse;
import com.project.lovable_clone.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse getProjectById(Long userId, Long id);

    ProjectResponse createProject(Long userId, ProjectRequest projectRequest);

    ProjectResponse updateProject(Long id, ProjectRequest projectRequest, Long userId);

    void softDelete(Long id, Long userId);

    List<ProjectSummaryResponse> getAllProject(Long userId);
}
