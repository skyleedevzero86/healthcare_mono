package com.sleekydz86.web.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtTokenUtils {

    private final Key KEY;
    private final String SECRET;

    public JwtTokenUtils(@Value("${token.secret}") String secret) {
        this.SECRET = secret;
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
        this.KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
