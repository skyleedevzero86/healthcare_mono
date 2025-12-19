package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.*;
import com.sleekydz86.service.auth.event.EventPublisher;
import com.sleekydz86.service.auth.event.UserEvent;
import com.sleekydz86.service.auth.exception.BusinessException;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.metrics.AuthMetrics;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import com.sleekydz86.service.auth.security.HealthcareEncryptionUtil;
import com.sleekydz86.service.auth.security.HealthcareEncryptionUtil.KeyType;
import com.sleekydz86.service.auth.util.DtoConverter;
import com.sleekydz86.service.auth.util.PasswordPolicyValidator;
import io.jsonwebtoken.Claims;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final EventPublisher eventPublisher;
    private final com.sleekydz86.service.auth.security.TokenBlacklistService tokenBlacklistService;
    private final AuthMetrics authMetrics;
    private final DtoConverter dtoConverter;
    private final PasswordPolicyValidator passwordPolicyValidator;

    @Transactional
    public Map<Object, Object> signin(SigninDto user) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", user.getUserId());
        MDC.put("requestId", requestId);
        MDC.put("operation", "signin");
        
        authMetrics.incrementSigninAttempts();
        Timer.Sample sample = authMetrics.startSigninProcessingTimer();
        
        try {
            log.info("사용자 로그인 처리 중: {}", user.getUserId());
            
            UserDto dto = userMapper.signin(user);
            if (dto == null) {
                authMetrics.incrementSigninFailure();
                log.error("로그인 실패 - 사용자를 찾을 수 없음: {}", user.getUserId());
                throw new BusinessException(
                    "아이디 또는 비밀번호가 올바르지 않습니다.",
                    ApiResultCode.AUTH_ERR
                );
            }

            if (user.getUserPw() != null && dto.getUserSalt() != null) {
                String hashedPassword = HealthcareEncryptionUtil.hashPassword(
                    user.getUserPw(),
                    dto.getUserSalt()
                );

                if (!hashedPassword.equals(dto.getUserPwEnc())) {
                    authMetrics.incrementSigninFailure();
                    log.error("로그인 실패 - 비밀번호 불일치: {}", user.getUserId());
                    throw new BusinessException(
                        "아이디 또는 비밀번호가 올바르지 않습니다.",
                        ApiResultCode.AUTH_ERR
                    );
                }
            }

            String userId = user.getUserId();
            String userRole = dto.getUserRoleFk();
            String source = user.getSource();
            JwtTokenDto tokenInfo = jwtTokenProvider.generateToken(userId, userRole, source);
            Map<Object, Object> map = dtoConverter.convertToMap(tokenInfo);
            map.put("userId", dto.getUserId());
            map.put("userNm", dto.getUserNm());

            String encryptedRefreshToken = HealthcareEncryptionUtil.encrypt(
                tokenInfo.getRefreshToken(),
                KeyType.AUTH
            );
            userMapper.updateToken(userId, userRole, source, encryptedRefreshToken);
            
            authMetrics.incrementSigninSuccess();
            authMetrics.incrementTokenGenerated();
            
            UserEvent loginEvent = new UserEvent(
                UUID.randomUUID().toString(),
                "USER_LOGIN",
                userId,
                Map.of("role", userRole, "source", source),
                LocalDateTime.now()
            );
            eventPublisher.publishUserEvent(loginEvent);
            
            log.info("로그인 처리 완료: {}", userId);
            return map;
        } catch (BusinessException e) {
            authMetrics.incrementSigninFailure();
            throw e;
        } catch (Exception e) {
            authMetrics.incrementSigninFailure();
            log.error("로그인 처리 중 오류 발생: {}", user.getUserId(), e);
            throw new BusinessException(
                "로그인 처리 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        } finally {
            sample.stop(authMetrics.getSigninProcessingTime());
            MDC.clear();
        }
    }

    @Transactional
    public int signup(SignupDto user) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("userId", user.getUserId());
        MDC.put("requestId", requestId);
        MDC.put("operation", "signup");
        
        authMetrics.incrementSignupAttempts();
        Timer.Sample sample = authMetrics.startSignupProcessingTimer();
        
        try {
            log.info("회원가입 처리 중: {}", user.getUserId());
            
            if (user.getUserPw() != null) {
                PasswordPolicyValidator.ValidationResult validationResult = passwordPolicyValidator.validate(user.getUserPw());
                if (!validationResult.isValid()) {
                    throw new BusinessException(
                        validationResult.getMessage(),
                        ApiResultCode.PARAM_VALID_ERR
                    );
                }
                
                String salt = HealthcareEncryptionUtil.generateSalt();
                String hashedPassword = HealthcareEncryptionUtil.hashPassword(user.getUserPw(), salt);
                user.setUserPwEnc(hashedPassword);
                user.setUserSalt(salt);
            }

            if (user.getBirthEnc() != null) {
                user.setBirthEnc(HealthcareEncryptionUtil.encrypt(user.getBirthEnc(), KeyType.USER));
            }
            if (user.getTelNumEnc() != null) {
                user.setTelNumEnc(HealthcareEncryptionUtil.encrypt(user.getTelNumEnc(), KeyType.USER));
            }
            
            int result = userMapper.signup(user);
            
            if (result > 0) {
                authMetrics.incrementSignupSuccess();
                
                UserEvent signupEvent = new UserEvent(
                    UUID.randomUUID().toString(),
                    "USER_CREATED",
                    user.getUserId(),
                    user,
                    LocalDateTime.now()
                );
                eventPublisher.publishUserEvent(signupEvent);
                
                log.info("회원가입 처리 완료: {}", user.getUserId());
            } else {
                log.warn("회원가입 실패 - 데이터베이스 삽입 결과 0: {}", user.getUserId());
                throw new BusinessException(
                    "회원가입에 실패했습니다.",
                    ApiResultCode.INSERT_FAIL
                );
            }
            
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생: {}", user.getUserId(), e);
            throw new BusinessException(
                "회원가입 처리 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        } finally {
            sample.stop(authMetrics.getSignupProcessingTime());
            MDC.clear();
        }
    }

    public boolean duplicateId(UserDto dto) {
        if (userMapper.duplicateId(dto) != null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean duplicateEmail(UserDto dto) {
        if (userMapper.duplicateEmail(dto) != null) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public void logout(String token) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "logout");
        
        try {
            String userId = com.sleekydz86.service.auth.util.UserContext.getUserId();
            String userRole = com.sleekydz86.service.auth.util.UserContext.getUserRole();
            String source = com.sleekydz86.service.auth.util.UserContext.getUserSource();
            
            if (userId == null || userRole == null || source == null) {
                throw new BusinessException(
                    "사용자 정보가 없습니다.",
                    ApiResultCode.AUTH_ERR
                );
            }
            
            MDC.put("userId", userId);
            
            log.info("로그아웃 처리 중: {}", userId);
            
            userMapper.updateToken(userId, userRole, source, null);
            
            if (token != null && !token.isEmpty()) {
                try {
                    Claims claims = jwtTokenProvider.parseClaims(token);
                    long expirationTime = claims.getExpiration() != null 
                        ? claims.getExpiration().getTime() - System.currentTimeMillis()
                        : 3600000;
                    if (expirationTime > 0) {
                        tokenBlacklistService.addToBlacklist(token, expirationTime);
                    }
                } catch (Exception e) {
                    log.warn("토큰 파싱 실패, 기본 만료 시간 사용: {}", e.getMessage());
                    tokenBlacklistService.addToBlacklist(token, 3600000);
                }
            }
            
            authMetrics.incrementLogoutCount();
            
            UserEvent logoutEvent = new UserEvent(
                UUID.randomUUID().toString(),
                "USER_LOGOUT",
                userId,
                Map.of("role", userRole, "source", source),
                LocalDateTime.now()
            );
            eventPublisher.publishUserEvent(logoutEvent);
            
            log.info("로그아웃 처리 완료: {}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            throw new BusinessException(
                "로그아웃 처리 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    public JwtTokenDto refresh(String accessToken, String refreshToken) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "refresh");
        
        try {
            String userId = com.sleekydz86.service.auth.util.UserContext.getUserId();
            String userRole = com.sleekydz86.service.auth.util.UserContext.getUserRole();
            String source = com.sleekydz86.service.auth.util.UserContext.getUserSource();
            
            if (userId == null || userRole == null || source == null) {
                throw new BusinessException(
                    "사용자 정보가 없습니다.",
                    ApiResultCode.AUTH_ERR
                );
            }
            
            MDC.put("userId", userId);
            
            log.info("토큰 갱신 처리 중: {}", userId);

            String originRefreshToken = userMapper.selectRefreshToken(userId, userRole, source);
            if (originRefreshToken == null || "".equals(originRefreshToken)) {
                log.error("유효하지 않은 리프레시 토큰 - 토큰을 찾을 수 없음: {}", userId);
                throw new BusinessException(
                    "유효하지 않은 리프레시 토큰입니다.",
                    ApiResultCode.INVALID_JWT_TOKEN_ERR
                );
            }

            jwtTokenProvider.validateRefreshToken(refreshToken);
            if (originRefreshToken.equals(refreshToken)) {
                JwtTokenDto tokenInfo = jwtTokenProvider.generateToken(userId, userRole, source);
                userMapper.updateToken(userId, userRole, source, tokenInfo.getRefreshToken());
                
                authMetrics.incrementTokenRefreshed();
                
                log.info("토큰 갱신 완료: {}", userId);
                return tokenInfo;
            } else {
                log.error("유효하지 않은 리프레시 토큰 - 토큰 불일치: {}", userId);
                throw new BusinessException(
                    "유효하지 않은 리프레시 토큰입니다.",
                    ApiResultCode.INVALID_JWT_TOKEN_ERR
                );
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            throw new BusinessException(
                "토큰 갱신 중 오류가 발생했습니다.",
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        } finally {
            MDC.clear();
        }
    }

    public List<Map<String, Object>> searchDoctor(UserDto dto) {
        return userMapper.searchDoctor(dto);
    }

    public List<Map<String, Object>> searchParent(UserDto dto) {
        return userMapper.searchParent(dto);
    }

    public int insUserAuth(@Valid SignupDto user) {
        return userMapper.insUserAuth(user);
    }

    public int insDoctorMapping(@Valid SignupDto user) {
        return userMapper.insDoctorMapping(user);
    }

    public int insGuardianMapping(@Valid SignupDto user) {
        return userMapper.insGuardianMapping(user);
    }

    public Map<String, Object> findUserId(FindDto dto) {
        return userMapper.findUserId(dto);
    }

    public Map<String, Object> findUserPw(FindDto dto) {
        return userMapper.findUserPw(dto);
    }

    public int updateUserPw(FindDto dto) {
        return userMapper.updateUserPw(dto);
    }

    public Integer getUserSeq(String userId) {
        return userMapper.getUserSeq(userId);
    }

}
