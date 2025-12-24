package com.sleekydz86.service.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class RoleBasedAccessControl {

    private static final Set<String> PATIENT_ACCESSIBLE = new HashSet<>(Arrays.asList(
        "/healthcare/v1/healthInfo",
        "/healthcare/v1/insertHealthInfo",
        "/healthcare/v1/realtimeBiodata",
        "/community/v1/findBoard",
        "/community/v1/findBoardList"
    ));

    private static final Set<String> GUARDIAN_ACCESSIBLE = new HashSet<>(Arrays.asList(
        "/healthcare/v1/healthInfo",
        "/healthcare/v1/realtimeBiodata",
        "/management/v1/userInfo"
    ));

    private static final Set<String> DOCTOR_ACCESSIBLE = new HashSet<>(Arrays.asList(
        "/healthcare/v1/healthInfo",
        "/healthcare/v1/realtimeBiodata",
        "/management/v1/userInfo",
        "/management/v1/search_userList",
        "/management/v1/healthUserList"
    ));

    private static final Set<String> ADMIN_ACCESSIBLE = new HashSet<>(Arrays.asList(
        "/management/v1/**",
        "/healthcare/v1/**"
    ));

    public boolean hasAccess(String role, String resourcePath) {
        Role userRole = Role.fromString(role);
        if (userRole == null) {
            log.warn("알 수 없는 역할: {}", role);
            return false;
        }

        if (userRole == Role.ADMIN) {
            return true;
        }

        String normalizedPath = normalizePath(resourcePath);

        switch (userRole) {
            case PATIENT:
                return PATIENT_ACCESSIBLE.stream().anyMatch(normalizedPath::startsWith);
            case GUARDIAN:
                return GUARDIAN_ACCESSIBLE.stream().anyMatch(normalizedPath::startsWith) ||
                       PATIENT_ACCESSIBLE.stream().anyMatch(normalizedPath::startsWith);
            case DOCTOR:
                return DOCTOR_ACCESSIBLE.stream().anyMatch(normalizedPath::startsWith) ||
                       PATIENT_ACCESSIBLE.stream().anyMatch(normalizedPath::startsWith);
            default:
                return false;
        }
    }

    public boolean hasAnyRoleAccess(Set<String> roles, String resourcePath) {
        return roles.stream().anyMatch(role -> hasAccess(role, resourcePath));
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String normalized = path.split("\\?")[0];
        normalized = normalized.replaceAll("/v\\d+/", "/");
        return normalized;
    }

    public boolean isAdmin(String role) {
        return Role.ADMIN == Role.fromString(role);
    }

    public boolean isDoctor(String role) {
        Role userRole = Role.fromString(role);
        return userRole == Role.DOCTOR || userRole == Role.ADMIN;
    }
}

