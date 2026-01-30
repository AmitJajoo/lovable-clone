package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.dto.project.FileContentResponse;
import com.project.lovable_clone.dto.project.FileNode;
import com.project.lovable_clone.entity.Project;
import com.project.lovable_clone.entity.ProjectFile;
import com.project.lovable_clone.error.ResourceNotFoundException;
import com.project.lovable_clone.mapper.ProjectFileMapper;
import com.project.lovable_clone.repository.ProjectFileRepository;
import com.project.lovable_clone.repository.ProjectRepository;
import com.project.lovable_clone.service.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private static final String BUCKET_NAME = "projects";
    private final ProjectRepository projectRepository;
    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    @Override
    public List<FileNode> getFileTree(Long projectId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFileList);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;

        try (
            InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectName)
                            .build())) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new FileContentResponse(path, content);
        } catch (Exception e) {
            log.error("Failed to read file: {}/{}", projectId, path, e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("projectId", projectId.toString()));

        String cleanPath = normalizePath(filePath);
        String objectKey = projectId + "/" + cleanPath;

        try (InputStream inputStream =
                     new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectKey)
                            .stream(inputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                            .contentType(determineContentType(cleanPath))
                            .build()
            );

            ProjectFile file = projectFileRepository
                    .findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);

            log.info("File saved successfully: {}", objectKey);

        } catch (Exception e) {
            log.error("Error saving file: {}", objectKey, e);
            throw new IllegalStateException("Failed to save file: " + cleanPath, e);
        }
    }

    private String normalizePath(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("filePath cannot be empty");
        }
        return filePath.startsWith("/") ? filePath.substring(1) : filePath;
    }

    private String determineContentType(String filePath) {
        String type = URLConnection.guessContentTypeFromName(filePath);
        if (type != null) return type;

        if (filePath.endsWith(".tsx") || filePath.endsWith(".ts") || filePath.endsWith(".jsx"))
            return "text/javascript";
        if (filePath.endsWith(".json"))
            return "application/json";
        if (filePath.endsWith(".css"))
            return "text/css";
        if (filePath.endsWith(".html"))
            return "text/html";

        return "text/plain";
    }

}
