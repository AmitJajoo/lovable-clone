package com.project.lovable_clone.service;

import com.project.lovable_clone.dto.project.ProjectRequest;
import com.project.lovable_clone.dto.project.ProjectResponse;
import com.project.lovable_clone.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(ProjectRequest projectRequest);

    ProjectResponse updateProject(Long id, ProjectRequest projectRequest);

    void softDelete(Long id);

    List<ProjectSummaryResponse> getAllProject();
}
