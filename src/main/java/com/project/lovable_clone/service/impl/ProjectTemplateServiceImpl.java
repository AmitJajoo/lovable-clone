package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.entity.Project;
import com.project.lovable_clone.entity.ProjectFile;
import com.project.lovable_clone.error.ResourceNotFoundException;
import com.project.lovable_clone.repository.ProjectFileRepository;
import com.project.lovable_clone.repository.ProjectRepository;
import com.project.lovable_clone.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private final static String TEMPLATE_BUCKET = "starter-project";
    private final static String TARGET_BUCKET = "projects";
    private final static String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";

    @Override
    public void initializeProjectFromTemplate(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("project", projectId.toString())
                );

        log.info("Initializing project {} from template {}", projectId, TEMPLATE_NAME);

        List<ProjectFile> filesToSave = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();

                // Skip directories
                if (item.isDir()) {
                    continue;
                }

                String sourceKey = item.objectName();
                String relativePath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destinationKey = projectId + "/" + relativePath;

                // Copy object in MinIO
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destinationKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                // Prepare DB entity
                ProjectFile projectFile = ProjectFile.builder()
                        .project(project)
                        .path(relativePath)
                        .minioObjectKey(destinationKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(projectFile);
            }

            // Batch save (much faster)
            projectFileRepository.saveAll(filesToSave);

            log.info("Project {} initialized successfully with {} files",
                    projectId, filesToSave.size());

        } catch (Exception e) {
            log.error("Failed to initialize project {} from template", projectId, e);
            throw new RuntimeException("Project initialization failed", e);
        }
    }
}
