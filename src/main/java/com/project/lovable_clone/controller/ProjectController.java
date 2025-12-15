package com.project.lovable_clone.controller;

import com.project.lovable_clone.dto.project.ProjectRequest;
import com.project.lovable_clone.dto.project.ProjectResponse;
import com.project.lovable_clone.dto.project.ProjectSummaryResponse;
import com.project.lovable_clone.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {

        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@RequestBody @Valid ProjectRequest projectRequest, @PathVariable Long id) {
        return ResponseEntity.ok(projectService.updateProject(id, projectRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getAllProject() {
        return ResponseEntity.ok(projectService.getAllProject());
    }
}
