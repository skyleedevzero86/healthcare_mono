package com.sleekydz86.service.llm.infrastructure.adapter.conversation;

import com.sleekydz86.service.llm.domain.model.ConversationId;
import com.sleekydz86.service.llm.domain.model.ConversationMessage;
import com.sleekydz86.service.llm.ports.outbound.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisConversationRepository implements ConversationRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CONVERSATION_KEY_PREFIX = "llm:conversation:";
    private static final String MESSAGE_KEY_PREFIX = "llm:message:";
    private static final int CONVERSATION_TTL_HOURS = 24;
    private static final int MAX_MESSAGES = 50;

    @Override
    public void createConversation(ConversationId conversationId, String userId) {
        String key = CONVERSATION_KEY_PREFIX + conversationId.getValue();
        redisTemplate.opsForValue().set(key, userId, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);
        log.debug("대화 세션 생성: conversationId={}, userId={}", conversationId, userId);
    }

    @Override
    public void saveMessage(ConversationId conversationId, ConversationMessage message) {
        String key = MESSAGE_KEY_PREFIX + conversationId.getValue();

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);

        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > MAX_MESSAGES) {
            redisTemplate.opsForList().leftPop(key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ConversationMessage> getHistory(ConversationId conversationId) {
        String key = MESSAGE_KEY_PREFIX + conversationId.getValue();

        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return new ArrayList<>();
        }

        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages == null) {
            return new ArrayList<>();
        }

        List<ConversationMessage> result = new ArrayList<>();
        for (Object obj : messages) {
            if (obj instanceof ConversationMessage) {
                result.add((ConversationMessage) obj);
            }
        }

        return result;
    }

    @Override
    public void deleteConversation(ConversationId conversationId) {
        String convKey = CONVERSATION_KEY_PREFIX + conversationId.getValue();
        String msgKey = MESSAGE_KEY_PREFIX + conversationId.getValue();

        redisTemplate.delete(convKey);
        redisTemplate.delete(msgKey);

        log.debug("대화 세션 삭제: conversationId={}", conversationId);
    }
}

