package com.sleekydz86.web.client;

import com.sleekydz86.web.dto.User;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public User getCurrentUser(String token) {
        return null;
    }
}

