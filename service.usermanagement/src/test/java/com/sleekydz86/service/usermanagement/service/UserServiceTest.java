package com.sleekydz86.service.usermanagement.service;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import com.sleekydz86.service.usermanagement.global.util.PagingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PagingUtil pagingUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private UserhealthDto userhealthDto;

    @BeforeEach
    void setUp() {
        String uniqueId = "testUser_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        userDto = new UserDto();
        userDto.setUserId(uniqueId);
        userDto.setUserNm("테스트 사용자");
        userDto.setUserRoleFk("1");
        userDto.setPageIdx(1);
        userDto.setPageSize(10);

        userhealthDto = new UserhealthDto();
        userhealthDto.setAgeRange("20-30");
        userhealthDto.setGender("M");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        userDto = null;
        userhealthDto = null;
        reset(userMapper, pagingUtil);
    }

    @Test
    @DisplayName("사용자 리스트 조회 성공")
    void userList_Success() {
        when(userMapper.userListCount(any(UserDto.class))).thenReturn(100);
        when(userMapper.userList(any(UserDto.class))).thenReturn(new ArrayList<>());

        Object result = userService.userList(userDto);

        assertThat(result).isNotNull();
        verify(userMapper, times(1)).userListCount(userDto);
        verify(userMapper, times(1)).userList(userDto);
    }

    @Test
    @DisplayName("보호자 리스트 조회 성공")
    void parentList_Success() {
        when(userMapper.parentListCount(any(UserDto.class))).thenReturn(50);
        when(userMapper.parentList(any(UserDto.class))).thenReturn(new ArrayList<>());

        Object result = userService.parentList(userDto);

        assertThat(result).isNotNull();
        verify(userMapper, times(1)).parentListCount(userDto);
        verify(userMapper, times(1)).parentList(userDto);
    }

    @Test
    @DisplayName("의사 리스트 조회 성공")
    void doctorList_Success() {
        when(userMapper.doctorListCount(any(UserDto.class))).thenReturn(30);
        when(userMapper.doctorList(any(UserDto.class))).thenReturn(new ArrayList<>());

        Object result = userService.doctorList(userDto);

        assertThat(result).isNotNull();
        verify(userMapper, times(1)).doctorListCount(userDto);
        verify(userMapper, times(1)).doctorList(userDto);
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void userInfo_Success() throws Exception {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("userId", "testUser");
        expectedData.put("userNm", "테스트 사용자");

        when(userMapper.userInfo(any(UserDto.class))).thenReturn(expectedData);

        Map<String, Object> result = userService.userInfo(userDto);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("userId");
        verify(userMapper, times(1)).userInfo(userDto);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUserInfo_Success() {
        when(userMapper.searchParentCount(any(UserDto.class))).thenReturn(0);
        when(userMapper.updateUserInfo(any(UserDto.class))).thenReturn(1);

        int result = userService.updateUserInfo(userDto);

        assertThat(result).isEqualTo(1);
        verify(userMapper, times(1)).updateUserInfo(userDto);
    }

    @Test
    @DisplayName("사용자 정보 삭제 성공")
    void deleteUserInfo_Success() {
        when(userMapper.deleteUserInfo(any(UserDto.class))).thenReturn(1);

        int result = userService.deleteUserInfo(userDto);

        assertThat(result).isEqualTo(1);
        verify(userMapper, times(1)).deleteUserInfo(userDto);
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePasswd_Success() {
        when(userMapper.updatePasswd(any(UserDto.class))).thenReturn(1);

        int result = userService.updatePasswd(userDto);

        assertThat(result).isEqualTo(1);
        verify(userMapper, times(1)).updatePasswd(userDto);
    }

    @Test
    @DisplayName("의사 검색 성공")
    void searchDoctor_Success() {
        List<Map<String, Object>> expectedList = new ArrayList<>();
        Map<String, Object> doctor = new HashMap<>();
        doctor.put("userNm", "의사");
        expectedList.add(doctor);

        when(userMapper.searchDoctor(any(UserDto.class))).thenReturn(expectedList);

        List<Map<String, Object>> result = userService.searchDoctor(userDto);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(userMapper, times(1)).searchDoctor(userDto);
    }

    @Test
    @DisplayName("보호자 검색 성공")
    void searchParent_Success() {
        List<Map<String, Object>> expectedList = new ArrayList<>();
        Map<String, Object> parent = new HashMap<>();
        parent.put("userNm", "보호자");
        expectedList.add(parent);

        when(userMapper.searchParent(any(UserDto.class))).thenReturn(expectedList);

        List<Map<String, Object>> result = userService.searchParent(userDto);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(userMapper, times(1)).searchParent(userDto);
    }

    @Test
    @DisplayName("연령별 평균 헬스 정보 조회 성공")
    void ageavgHealthinfo_Success() {
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("avgHeartrate", 72);
        expectedData.put("avgTemperature", 36.5);

        when(userMapper.ageavgHealthinfo(any(UserhealthDto.class))).thenReturn(expectedData);

        Map<String, Object> result = userService.ageavgHealthinfo(userhealthDto);

        assertThat(result).isNotNull();
        assertThat(result).containsKey("avgHeartrate");
        verify(userMapper, times(1)).ageavgHealthinfo(userhealthDto);
    }
}

