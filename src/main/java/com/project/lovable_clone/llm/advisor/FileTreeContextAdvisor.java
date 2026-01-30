package com.project.lovable_clone.llm.advisor;

import com.project.lovable_clone.dto.project.FileNode;
import com.project.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain streamAdvisorChain) {
        Map<String, Object> context = request.context();

        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());
        ChatClientRequest augmentedChatClientRequest = augementRequestWithFileTree(request, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augementRequestWithFileTree(ChatClientRequest request, Long projectId) {
        List<Message> incomingMessage = request.prompt().getInstructions();

        Message systemMessage = incomingMessage.stream().filter(
                m -> m.getMessageType() == MessageType.SYSTEM
        ).findFirst().orElse(null);

        List<Message> userMessages = incomingMessage.stream().filter(
                m -> m.getMessageType() != MessageType.SYSTEM
        ).toList();

        List<Message> allMessage = new ArrayList<>();

        //add original system prompt
        if (systemMessage != null) allMessage.add(systemMessage);

        List<FileNode> fileTree = projectFileService.getFileTree(projectId);
        String fileTreeContent = "\n\n ----- FILE TREE -----\n\n" + fileTree.toString();
        allMessage.add(new SystemMessage(fileTreeContent));
        allMessage.addAll(userMessages);
        return request.mutate()
                .prompt(new Prompt(allMessage, request.prompt().getOptions()))
                .build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
