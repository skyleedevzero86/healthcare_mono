package com.sleekydz86.service.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.auth.dto.*;
import com.sleekydz86.service.auth.mapper.UserMapper;
import com.sleekydz86.service.auth.provider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public Map<Object, Object> signin(SigninDto user) throws Exception {
        UserDto dto = userMapper.signin(user);
        if (dto == null) {
            throw new AuthenticationException();
        }
        String userId = user.getUserId();
        String userRole = dto.getUserRoleFk();
        String source = user.getSource();
        JwtTokenDto tokenInfo = jwtTokenProvider.generateToken(userId, userRole, source);
        ObjectMapper mapper = new ObjectMapper();
        Map<Object, Object> map = mapper.convertValue(tokenInfo, HashMap.class);
        map.put("userId", dto.getUserId());
        map.put("userNm", dto.getUserNm());

        userMapper.updateToken(userId, userRole, source, tokenInfo.getRefreshToken());
        return map;
    }

    public int signup(SignupDto user) throws Exception {
        return userMapper.signup(user);
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

    public void logout(String token) throws Exception {
        jwtTokenProvider.validateToken(token);
        Claims claims = jwtTokenProvider.parseClaims(token);

        String userId = (String) claims.get("id");
        String userRole = (String) claims.get("role");
        String source = (String) claims.get("source");
        userMapper.updateToken(userId, userRole, source, null);
    }

    public JwtTokenDto refresh(String accessToken, String refreshToken) throws Exception {
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        String userId = (String) claims.get("id");
        String userRole = (String) claims.get("role");
        String source = (String) claims.get("source");

        String originRefreshToken = userMapper.selectRefreshToken(userId, userRole, source);
        if (originRefreshToken == null || "".equals(originRefreshToken)) {
            throw new AuthenticationException();
        }

        jwtTokenProvider.validateRefreshToken(refreshToken);
        if (originRefreshToken.equals(refreshToken)) {
            JwtTokenDto tokenInfo = jwtTokenProvider.generateToken(userId, userRole, source);
            userMapper.updateToken(userId, userRole, source, tokenInfo.getRefreshToken());
            return tokenInfo;
        } else {
            throw new AuthenticationException();
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

}
