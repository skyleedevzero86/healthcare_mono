package com.sleekydz86.service.auth.security;

import java.time.LocalDateTime;

public class KeyInfo {
    private String currentKey;
    private String previousKey;
    private LocalDateTime createdAt;

    public KeyInfo() {
    }

    public KeyInfo(String currentKey, String previousKey, LocalDateTime createdAt) {
        this.currentKey = currentKey;
        this.previousKey = previousKey;
        this.createdAt = createdAt;
    }

    public String getCurrentKey() {
        return currentKey;
    }

    public void setCurrentKey(String currentKey) {
        this.currentKey = currentKey;
    }

    public String getPreviousKey() {
        return previousKey;
    }

    public void setPreviousKey(String previousKey) {
        this.previousKey = previousKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

