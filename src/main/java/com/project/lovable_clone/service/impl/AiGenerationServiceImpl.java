package com.project.lovable_clone.service.impl;

import com.project.lovable_clone.llm.PromptUtils;
import com.project.lovable_clone.llm.advisor.FileTreeContextAdvisor;
import com.project.lovable_clone.llm.tool.CodeGenerationTools;
import com.project.lovable_clone.security.AuthUtil;
import com.project.lovable_clone.service.AiGenerationService;
import com.project.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final AuthUtil authUtil;
    private final ChatClient chatClient;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile(
            "<file path=\"([^\"]+)\">(.*?)</file>",
            Pattern.DOTALL
    );


    @Override
//    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {

        Long userId = authUtil.getCurrentUserId();

        createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);
        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);

                })
                .stream()
                .chatResponse()

                // 1️⃣ Never trust response
                .filter(Objects::nonNull)

                // 2️⃣ Extract result safely
                .map(response -> response.getResult())
                .filter(Objects::nonNull)

                // 3️⃣ Extract output safely
                .map(result -> result.getOutput())
                .filter(Objects::nonNull)

                // 4️⃣ Extract text safely
                .map(output -> output.getText())
                .filter(text -> text != null && !text.isBlank())

                // 5️⃣ Side-effect: accumulate response
                .doOnNext(text -> fullResponseBuffer.append(text))

                // 6️⃣ Final processing (non-blocking)
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() ->
                            parseAndSaveFiles(fullResponseBuffer.toString(), projectId)
                    );
                })

                // 7️⃣ Never kill stream on chunk error
                .onErrorContinue((ex, value) ->
                        log.error("Streaming chunk failed for projectId={}", projectId, ex)
                );
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
//        String dummy = """
//                <message>I'm going to read the files and generate the code</message>
//                <file path="src/App.jsx">
//                    import App from './App.jsx'
//                    ......
//                </file>
//                """;

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId, filePath, fileContent);
        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {
    }
}
