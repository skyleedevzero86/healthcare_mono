package com.sleekydz86.service.auth.provider;

import com.sleekydz86.service.auth.dto.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey KEY;

    private long ACCESS_EXPIRED_TIME;

    private final long REFRESH_EXPIRED_TIME;

    private final String SECRET;

    public JwtTokenProvider(
            @Value("${token.secret}") String secret,
            @Value("${token.access-expired-time}") long accessExpiredTime,
            @Value("${token.refresh-expired-time}") long refreshExpiredTime) {
        this.SECRET = secret;
        this.ACCESS_EXPIRED_TIME = accessExpiredTime;
        this.REFRESH_EXPIRED_TIME = refreshExpiredTime;
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        this.KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokenDto generateToken(String userId, String userRole, String source) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = null;
        Date refreshTokenExpiresIn = null;
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");

        if (source.equals("M")) {
            try {
                accessTokenExpiresIn = dtFormat.parse("99991231");
                refreshTokenExpiresIn = dtFormat.parse("99991231");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            accessTokenExpiresIn = new Date(now + ACCESS_EXPIRED_TIME);
            refreshTokenExpiresIn = new Date(now + REFRESH_EXPIRED_TIME);
        }

        String accessToken = Jwts.builder()
                .claim("id", userId)
                .claim("role", userRole)
                .claim("source", source)
                .setExpiration(accessTokenExpiresIn)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .claim("key", UUID.randomUUID())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean validateToken(String token) throws Exception {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            Claims claims = parseClaims(token);

            if (claims.get("id") == null || "".equals(claims.get("id"))) {
                throw new UnsupportedJwtException("권한 정보가 없는 토큰입니다.");
            } else if (claims.get("role") == null || "".equals(claims.get("role"))) {
                throw new UnsupportedJwtException("권한 정보가 없는 토큰입니다.");
            } else if (claims.get("source") == null || "".equals(claims.get("source"))) {
                throw new UnsupportedJwtException("권한 정보가 없는 토큰입니다.");
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new UnsupportedJwtException("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new UnsupportedJwtException("JWT claims string is empty");
        } catch (Exception e) {
            throw new UnsupportedJwtException("JWT Unkown Error");
        }
    }

    public boolean validateRefreshToken(String token) throws Exception {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            Claims claims = parseClaims(token);

            if (claims.get("key") == null || "".equals(claims.get("key"))) {
                throw new UnsupportedJwtException("권한 정보가 없는 토큰입니다.");
            }
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new UnsupportedJwtException("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new UnsupportedJwtException("JWT claims string is empty");
        } catch (Exception e) {
            throw new UnsupportedJwtException("JWT Unkown Error");
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
