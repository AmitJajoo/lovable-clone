package com.project.lovable_clone.entity;

import com.project.lovable_clone.enums.MessageRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    Long id;
    ChatSession chatSession;
    MessageRole role;
    String content;
    String toolCalls;
    Integer tokenUsed;
    Instant createdAt;

}
