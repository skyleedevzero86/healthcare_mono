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
    
    private final com.sleekydz86.service.auth.security.TokenBlacklistService tokenBlacklistService;

    public JwtTokenProvider(
            @Value("${token.secret}") String secret,
            @Value("${token.access-expired-time}") long accessExpiredTime,
            @Value("${token.refresh-expired-time}") long refreshExpiredTime,
            com.sleekydz86.service.auth.security.TokenBlacklistService tokenBlacklistService) {
        this.SECRET = secret;
        this.ACCESS_EXPIRED_TIME = accessExpiredTime;
        this.REFRESH_EXPIRED_TIME = refreshExpiredTime;
        this.tokenBlacklistService = tokenBlacklistService;
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
                log.error("날짜 파싱 오류", e);
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
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("토큰이 비어있습니다.");
        }
        
        if (token.length() > 2048) {
            throw new IllegalArgumentException("토큰이 너무 깁니다.");
        }
        
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("블랙리스트에 등록된 토큰 사용 시도");
                throw new UnsupportedJwtException("로그아웃된 토큰입니다.");
            }
            
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration() != null && claims.getExpiration().getTime() < System.currentTimeMillis()) {
                log.warn("만료된 토큰 사용 시도");
                throw new ExpiredJwtException(null, claims, "만료된 토큰입니다.");
            }

            String userId = claims.get("id", String.class);
            String userRole = claims.get("role", String.class);
            String source = claims.get("source", String.class);

            if (userId == null || userId.trim().isEmpty()) {
                throw new UnsupportedJwtException("사용자 ID가 없는 토큰입니다.");
            }
            
            if (userRole == null || userRole.trim().isEmpty()) {
                throw new UnsupportedJwtException("역할 정보가 없는 토큰입니다.");
            }
            
            if (source == null || source.trim().isEmpty()) {
                throw new UnsupportedJwtException("소스 정보가 없는 토큰입니다.");
            }

            if (tokenBlacklistService.checkTokenReuse(token, userId)) {
                log.warn("토큰 재사용 시도 감지: userId={}", userId);
                throw new UnsupportedJwtException("토큰 재사용이 감지되었습니다.");
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("유효하지 않은 JWT 토큰", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims 문자열이 비어있습니다.", e);
            throw new UnsupportedJwtException("JWT claims 문자열이 비어있습니다.");
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 알 수 없는 오류", e);
            throw new UnsupportedJwtException("JWT 토큰 검증 중 오류가 발생했습니다.");
        }
    }

    public boolean validateRefreshToken(String token) throws Exception {
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            Claims claims = parseClaims(token);

            if (claims.get("key") == null || "".equals(claims.get("key"))) {
                throw new UnsupportedJwtException("권한 정보가 없는 토큰입니다.");
            }
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 JWT 토큰", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰", e);
            throw new UnsupportedJwtException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims 문자열이 비어있습니다.", e);
            throw new UnsupportedJwtException("JWT claims 문자열이 비어있습니다.");
        } catch (Exception e) {
            throw new UnsupportedJwtException("JWT 알 수 없는 오류가 발생했습니다.");
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
