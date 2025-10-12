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
    public Map<Object, Object> signin(SigninDto user) throws Exception;

    public int signup(SignupDto user) throws Exception;

    public boolean duplicateId(UserDto dto);

    public boolean duplicateEmail(UserDto dto);

    public void logout(String token) throws Exception;

    public JwtTokenDto refresh(String accessToken, String refreshToken) throws Exception;

    public List<Map<String, Object>> searchDoctor(UserDto dto);

    public List<Map<String, Object>> searchParent(UserDto dto);

    public int insUserAuth(@Valid SignupDto user);

    public int insDoctorMapping(@Valid SignupDto user);

    public int insGuardianMapping(@Valid SignupDto user);

    public Map<String, Object> findUserId(FindDto dto);

    public Map<String, Object> findUserPw(FindDto dto);

    public int updateUserPw(FindDto dto);
}
