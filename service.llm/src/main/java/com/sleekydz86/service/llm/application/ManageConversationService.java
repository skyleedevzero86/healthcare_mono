package com.sleekydz86.service.llm.application;

import com.sleekydz86.service.llm.domain.model.ConversationId;
import com.sleekydz86.service.llm.domain.model.ConversationMessage;
import com.sleekydz86.service.llm.ports.inbound.ManageConversationUseCase;
import com.sleekydz86.service.llm.ports.outbound.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageConversationService implements ManageConversationUseCase {

    private final ConversationRepository conversationRepository;

    @Override
    public ConversationId createConversation(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        ConversationId conversationId = ConversationId.generate();
        conversationRepository.createConversation(conversationId, userId);
        
        log.debug("대화 세션 생성: conversationId={}, userId={}", conversationId, userId);
        return conversationId;
    }

    @Override
    public void saveMessage(ConversationId conversationId, ConversationMessage message) {
        if (conversationId == null) {
            throw new IllegalArgumentException("대화 ID는 필수입니다.");
        }
        if (message == null || !message.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 메시지입니다.");
        }

        conversationRepository.saveMessage(conversationId, message);
        log.debug("메시지 저장: conversationId={}, role={}", conversationId, message.getRole());
    }

    @Override
    public List<ConversationMessage> getHistory(ConversationId conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("대화 ID는 필수입니다.");
        }

        return conversationRepository.getHistory(conversationId);
    }

    @Override
    public void deleteConversation(ConversationId conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("대화 ID는 필수입니다.");
        }

        conversationRepository.deleteConversation(conversationId);
        log.debug("대화 세션 삭제: conversationId={}", conversationId);
    }
}

