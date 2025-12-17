package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.FindDto;
import com.sleekydz86.service.auth.dto.JwtTokenDto;
import com.sleekydz86.service.auth.dto.SigninDto;
import com.sleekydz86.service.auth.dto.SignupDto;
import com.sleekydz86.service.auth.dto.UserDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<Object, Object> signin(SigninDto user);

    int signup(SignupDto user);

    boolean duplicateId(UserDto dto);

    boolean duplicateEmail(UserDto dto);

    void logout(String token);

    JwtTokenDto refresh(String accessToken, String refreshToken);

    public List<Map<String, Object>> searchDoctor(UserDto dto);

    public List<Map<String, Object>> searchParent(UserDto dto);

    public int insUserAuth(@Valid SignupDto user);

    public int insDoctorMapping(@Valid SignupDto user);

    public int insGuardianMapping(@Valid SignupDto user);

    public Map<String, Object> findUserId(FindDto dto);

    public Map<String, Object> findUserPw(FindDto dto);

    public int updateUserPw(FindDto dto);

    public Integer getUserSeq(String userId);
}
