package com.sleekydz86.service.llm.ports.inbound;

import com.sleekydz86.service.llm.domain.model.ConversationId;
import com.sleekydz86.service.llm.domain.model.ConversationMessage;

import java.util.List;

public interface ManageConversationUseCase {
    ConversationId createConversation(String userId);

    void saveMessage(ConversationId conversationId, ConversationMessage message);

    List<ConversationMessage> getHistory(ConversationId conversationId);

    void deleteConversation(ConversationId conversationId);
}

