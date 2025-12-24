package com.sleekydz86.service.usermanagement.service;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("UserService 통합 테스트")
class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("usermanagement_test")
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

    private UserDto userDto;
    private String uniqueSearchName;

    @BeforeEach
    void setUp() {
        uniqueSearchName = "검색_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        userDto = new UserDto();
        userDto.setUserRoleFk("1");
        userDto.setPageIndex(1);
        userDto.setPageSize(10);
    }

    @Test
    @DisplayName("사용자 리스트 조회 통합 테스트")
    void testUserListFlow() {
        Object result = userService.userList(userDto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("보호자 리스트 조회 통합 테스트")
    void testParentListFlow() {
        userDto.setUserRoleFk("2");
        Object result = userService.parentList(userDto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("의사 리스트 조회 통합 테스트")
    void testDoctorListFlow() {
        userDto.setUserRoleFk("3");
        Object result = userService.doctorList(userDto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("의사 및 보호자 검색 통합 테스트")
    void testSearchDoctorAndParent() {
        UserDto searchDto = new UserDto();
        searchDto.setUserNm(uniqueSearchName + "_의사");

        List<Map<String, Object>> doctorList = userService.searchDoctor(searchDto);
        assertThat(doctorList).isNotNull();

        searchDto.setUserNm(uniqueSearchName + "_보호자");
        List<Map<String, Object>> parentList = userService.searchParent(searchDto);
        assertThat(parentList).isNotNull();
    }

    @Test
    @DisplayName("연령별 평균 헬스 정보 조회 통합 테스트")
    void testAgeavgHealthinfo() {
        UserhealthDto userhealthDto = new UserhealthDto();
        userhealthDto.setAgeRange("20-30");
        userhealthDto.setGender("M");

        Map<String, Object> result = userService.ageavgHealthinfo(userhealthDto);
        assertThat(result).isNotNull();
    }
}

