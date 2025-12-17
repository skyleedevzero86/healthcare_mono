package com.sleekydz86.service.usermanagement.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EncryptionAuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionAuditLogger.class);

    public void logEncryption(String operation, String dataType, String userId, boolean success) {
        String logMessage = String.format(
                "암호화_감사: 작업=%s, 데이터유형=%s, 사용자ID=%s, 성공=%s, 시간=%s",
                operation, dataType, userId, success, LocalDateTime.now());

        if (success) {
            logger.info(logMessage);
        } else {
            logger.error(logMessage);
        }
    }

    public void logKeyRotation(String keyName, String oldKeyHash, String newKeyHash) {
        String logMessage = String.format(
                "키_로테이션: 키이름=%s, 이전키해시=%s, 새키해시=%s, 시간=%s",
                keyName, oldKeyHash, newKeyHash, LocalDateTime.now());

        logger.info(logMessage);
    }
}
