package com.sleekydz86.api.gateway.client;

import org.springframework.stereotype.Component;

@Component
public class AuthClientFallback implements AuthClient {

    @Override
    public Boolean validateToken(TokenRequest request) {
        return false;
    }
}

