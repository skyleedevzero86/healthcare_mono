package com.sleekydz86.service.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String role;
}

