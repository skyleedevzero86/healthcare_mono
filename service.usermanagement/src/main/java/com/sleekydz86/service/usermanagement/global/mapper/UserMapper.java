package com.sleekydz86.service.usermanagement.global.mapper;

import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<Map<String, Object>> userList(UserDto dto);

    List<Map<String, Object>> parentList(UserDto dto);

    List<Map<String, Object>> doctorList(UserDto dto);

    Map<String, Object> userInfo(UserDto dto);

    int updateUserInfo(UserDto dto);

    int deleteUserInfo(@Valid UserDto dto);

    int updatePasswd(@Valid UserDto dto);

    List<Map<String, Object>> searchDoctor(UserDto dto);

    List<Map<String, Object>> searchParent(UserDto dto);

    int doctorListCount(UserDto dto);

    int parentListCount(UserDto dto);

    int userListCount(UserDto dto);

    int manage_userList_cnt(UserDto dto);

    int updateGuardianMaping(UserDto dt);

    int updateDoctorMaping(@Valid UserDto dto);

    int searchParentCount(@Valid UserDto dto);

    int searchDoctorCount(@Valid UserDto dto);

    int deleteParentMapping(@Valid UserDto dto);

    List<Map<String, Object>> searchHealthUserList(UserDto dto);

    List<Map<String, Object>> searchguardianList(UserDto dto);

    List<Map<String, Object>> searchdoctorList(UserDto dto);

    List<Map<String, Object>> manage_userList(UserDto dto);

    List<Map<String, Object>> searchuserList(Map<String, Object> map);

    Map<String, Object> ageavgHealthinfo(UserhealthDto dto);

}