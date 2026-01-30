package com.project.lovable_clone.llm.tool;

import com.project.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;

    @Tool(
            name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under FILE_TREE."
    )
    public List<String> readFiles(
            @ToolParam(description = "List of relative path (eg. ['src/App.tsx'])")
            List<String> paths) {
        List<String> results = new ArrayList<>();

        for(String path: paths) {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            String content = projectFileService.getFileContent(projectId, cleanPath).content();

            results.add(String.format(
                    "----START OF FILE: %s----\n%s\n----END OF FILE----",
                    cleanPath, content
            ));
        }

        return results;
    }
}
