package com.sleekydz86.service.auth.security;

public enum Role {
    PATIENT("ROLE_PATIENT", "환자"),
    GUARDIAN("ROLE_GUARDIAN", "보호자"),
    DOCTOR("ROLE_DOCTOR", "의사"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String role) {
        if (role == null) {
            return null;
        }
        for (Role r : Role.values()) {
            if (r.authority.equalsIgnoreCase(role) || 
                r.name().equalsIgnoreCase(role) ||
                r.description.equals(role)) {
                return r;
            }
        }
        return null;
    }
}

