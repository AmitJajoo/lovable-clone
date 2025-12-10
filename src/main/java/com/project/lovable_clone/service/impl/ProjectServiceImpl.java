package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.project.ProjectRequest;
import com.project.lovable_clone.dto.project.ProjectResponse;
import com.project.lovable_clone.dto.project.ProjectSummaryResponse;
import com.project.lovable_clone.entity.Project;
import com.project.lovable_clone.entity.User;
import com.project.lovable_clone.mapper.ProjectMapper;
import com.project.lovable_clone.repository.ProjectRepository;
import com.project.lovable_clone.repository.UserRepository;
import com.project.lovable_clone.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    UserRepository userRepository;
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;

    @Override
    public ProjectResponse getProjectById(Long userId, Long id) {
        return null;
    }

    @Override
    public ProjectResponse createProject(Long userId, ProjectRequest projectRequest) {

        User owner = userRepository.findById(userId).orElseThrow();
        Project project = Project.builder()
                .name(projectRequest.name())
                .isPublic(false)
                .owner(owner)
                .build();

        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest projectRequest, Long userId) {
        return null;
    }

    @Override
    public void softDelete(Long id, Long userId) {

    }

    @Override
    public List<ProjectSummaryResponse> getAllProject(Long userId) {
        List<Project> projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toProjectSummaryResponseList(projects);
    }
}
