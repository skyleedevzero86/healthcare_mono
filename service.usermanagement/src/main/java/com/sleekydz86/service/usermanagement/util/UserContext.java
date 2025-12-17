package com.sleekydz86.service.usermanagement.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_SOURCE_HEADER = "X-User-Source";

    public static String getUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(USER_ID_HEADER);
    }

    public static String getUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(USER_ROLE_HEADER);
    }

    public static String getUserSource() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(USER_SOURCE_HEADER);
    }

    public static boolean hasUserInfo() {
        String userId = getUserId();
        String userRole = getUserRole();
        String source = getUserSource();
        return StringUtils.hasText(userId) && StringUtils.hasText(userRole) && StringUtils.hasText(source);
    }

    public static void validateUserInfo() {
        if (!hasUserInfo()) {
            throw new IllegalStateException("사용자 정보가 없습니다. Gateway에서 인증이 필요합니다.");
        }
    }

    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            log.debug("Request context를 가져올 수 없습니다", e);
        }
        return null;
    }
}


