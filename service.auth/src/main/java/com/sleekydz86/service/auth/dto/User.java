package com.sleekydz86.service.auth.dto;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String role;
}

