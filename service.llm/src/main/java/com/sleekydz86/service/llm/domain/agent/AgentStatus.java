package com.sleekydz86.service.llm.domain.agent;

public enum AgentStatus {
    IDLE,
    PLANNING,
    EXECUTING,
    THINKING,
    WAITING_FOR_TOOL,
    COMPLETED,
    FAILED,
    PAUSED
}

