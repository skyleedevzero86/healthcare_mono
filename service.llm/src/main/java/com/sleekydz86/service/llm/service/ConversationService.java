package com.sleekydz86.service.llm.service;

import com.sleekydz86.service.llm.dto.LLMResponse;
import com.sleekydz86.service.llm.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CONVERSATION_KEY_PREFIX = "llm:conversation:";
    private static final String MESSAGE_KEY_PREFIX = "llm:message:";
    private static final int CONVERSATION_TTL_HOURS = 24;

    public String createConversation(String userId) {
        String conversationId = UUID.randomUUID().toString();
        String key = CONVERSATION_KEY_PREFIX + conversationId;

        redisTemplate.opsForValue().set(key, userId, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);

        log.debug("대화 세션 생성: conversationId={}, userId={}", conversationId, userId);
        return conversationId;
    }

    public void saveMessage(String conversationId, String role, String content) {
        String key = MESSAGE_KEY_PREFIX + conversationId;

        Message message = Message.builder()
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);
        
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > 50) {
            redisTemplate.opsForList().leftPop(key);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Message> getHistory(String conversationId) {
        String key = MESSAGE_KEY_PREFIX + conversationId;

        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return new ArrayList<>();
        }

        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages == null) {
            return new ArrayList<>();
        }

        List<Message> result = new ArrayList<>();
        for (Object obj : messages) {
            if (obj instanceof Message) {
                result.add((Message) obj);
            }
        }

        return result;
    }

    public void deleteConversation(String conversationId) {
        String convKey = CONVERSATION_KEY_PREFIX + conversationId;
        String msgKey = MESSAGE_KEY_PREFIX + conversationId;

        redisTemplate.delete(convKey);
        redisTemplate.delete(msgKey);

        log.debug("대화 세션 삭제: conversationId={}", conversationId);
    }

    public List<String> getUserConversations(String userId) {
        return new ArrayList<>();
    }
}
