package com.sleekydz86.service.auth.service;

import com.sleekydz86.service.auth.dto.SigninDto;
import com.sleekydz86.service.auth.dto.SignupDto;
import com.sleekydz86.service.auth.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("UserService 통합 테스트")
class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("auth_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    private SignupDto signupDto;
    private String uniqueUserId;

    @BeforeEach
    void setUp() {
        uniqueUserId = "testUser_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        signupDto = SignupDto.builder()
                .userId(uniqueUserId)
                .userPwEnc("testPassword123")
                .userNm("통합테스트 사용자")
                .userRoleFk("1")
                .birthEnc("19900101")
                .telNumEnc("01012345678")
                .email("integration_" + System.currentTimeMillis() + "@test.com")
                .deptNm("IT")
                .height(170.0f)
                .weight(70.0f)
                .gender("M")
                .bloodType("A")
                .build();
    }

    @Test
    @DisplayName("회원가입 및 로그인 플로우 통합 테스트")
    void testSignupAndSigninFlow() throws Exception {
        UserDto checkDto = new UserDto();
        checkDto.setUserId(signupDto.getUserId());
        checkDto.setEmail(signupDto.getEmail());

        boolean duplicateId = userService.duplicateId(checkDto);
        assertThat(duplicateId).isFalse();

        boolean duplicateEmail = userService.duplicateEmail(checkDto);
        assertThat(duplicateEmail).isFalse();

        int signupResult = userService.signup(signupDto);
        assertThat(signupResult).isGreaterThan(0);

        Integer userSeq = userService.getUserSeq(signupDto.getUserId());
        assertThat(userSeq).isNotNull();
        assertThat(userSeq).isGreaterThan(0);

        SigninDto signinDto = SigninDto.builder()
                .userId(signupDto.getUserId())
                .userPwEnc(signupDto.getUserPwEnc())
                .userRoleFk("1")
                .source("web")
                .build();

        Map<Object, Object> signinResult = userService.signin(signinDto);
        assertThat(signinResult).isNotNull();
    }

    @Test
    @DisplayName("의사 및 보호자 검색 통합 테스트")
    void testSearchDoctorAndParent() {
        UserDto searchDto = new UserDto();
        searchDto.setUserNm("의사");

        var doctorList = userService.searchDoctor(searchDto);
        assertThat(doctorList).isNotNull();

        searchDto.setUserNm("보호자");
        var parentList = userService.searchParent(searchDto);
        assertThat(parentList).isNotNull();
    }
}

