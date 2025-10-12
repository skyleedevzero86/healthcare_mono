package com.sleekydz86.service.usermanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.usermanagement.dto.CamelHashMap;
import com.sleekydz86.service.usermanagement.dto.CommonPagingResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import com.sleekydz86.service.usermanagement.global.util.PaginationInfo;
import com.sleekydz86.service.usermanagement.global.util.PagingUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PagingUtil pagingUtil;

    public Object userList(UserDto dto) {
        int totalCount = userMapper.userListCount(dto);
        PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

        return CommonPagingResponse.builder()
                .list(userMapper.userList(dto))
                .totalCount(paginationInfo.getTotalRecordCount())
                .paginationInfo(paginationInfo)
                .build();
    }

    public Object parentList(UserDto dto) {
        int totalCount = userMapper.parentListCount(dto);
        PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

        return CommonPagingResponse.builder()
                .list(userMapper.parentList(dto))
                .totalCount(paginationInfo.getTotalRecordCount())
                .paginationInfo(paginationInfo)
                .build();
    }

    public Object doctorList(UserDto dto) {
        int totalCount = userMapper.doctorListCount(dto);
        PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);
        return CommonPagingResponse.builder()
                .list(userMapper.doctorList(dto))
                .totalCount(paginationInfo.getTotalRecordCount())
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public Object manage_userList(UserDto dto) {
        int totalCount = userMapper.manage_userList_cnt(dto);
        PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

        return CommonPagingResponse.builder()
                .list(userMapper.manage_userList(dto))
                .totalCount(paginationInfo.getTotalRecordCount())
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public List<Map<String, Object>> searchuserList(Map<String, Object> map) {
        return userMapper.searchuserList(map);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> userInfo(UserDto dto) throws Exception {
        Map<String, Object> result = userMapper.userInfo(dto);
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<CamelHashMap<String, Object>> res = new ArrayList<CamelHashMap<String, Object>>();
        if (result != null) {
            if (result.containsKey("guardian")) {
                if (result.get("guardian") != null && !result.get("guardian").equals("")) {
                    String str = (String) result.get("guardian");
                    ArrayList<Map<String, Object>> arr = mapper.readValue(str, ArrayList.class);
                    for (Map<String, Object> s : arr) {
                        CamelHashMap<String, Object> cm = mapper.convertValue(s, CamelHashMap.class);
                        res.add(cm);
                    }
                    result.put("guardian", res);
                }
            }
        }
        return result;
    }

    public int updateUserInfo(@Valid UserDto dto) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        String str = dto.getGuardian();
        ObjectMapper obj = new ObjectMapper();
        try {

            int cnt = userMapper.searchParentCount(dto);
            ArrayList<Integer> arr = new ArrayList<Integer>();
            if (str != null) {
                result = obj.readValue(str, ArrayList.class);
                for (Map<String, Object> dt : result) {
                    arr.add((int) dt.get("guardianSeq"));
                    dto.setGuardianSeq((int) dt.get("guardianSeq"));
                    dto.setGuardianId((String) dt.get("guardianId"));
                    userMapper.updateGuardianMaping(dto);
                }
            }
            if (arr.size() > 0)
                dto.setGuardianSeqArray(arr);
            int delCnt = userMapper.deleteParentMapping(dto);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (dto.getDoctorSeq() != 0 && dto.getDoctorId() != null && !dto.getDoctorId().equals("")) {
            userMapper.updateDoctorMaping(dto);
        }

        return userMapper.updateUserInfo(dto);
    }

    public int deleteUserInfo(@Valid UserDto dto) {
        return userMapper.deleteUserInfo(dto);
    }

    public int updatePasswd(@Valid UserDto dto) {
        return userMapper.updatePasswd(dto);
    }

    public List<Map<String, Object>> searchDoctor(UserDto dto) {
        return userMapper.searchDoctor(dto);
    }

    public List<Map<String, Object>> searchParent(UserDto dto) {
        return userMapper.searchParent(dto);
    }

    public List<Map<String, Object>> searchHealthUserList(Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        UserDto dto = mapper.convertValue(map, UserDto.class);
        return userMapper.searchHealthUserList(dto);
    }

    @Override
    public List<Map<String, Object>> searchdrguardianList(UserDto dto) {
        List<Map<String, Object>> doctorList = userMapper.searchdoctorList(dto);
        List<Map<String, Object>> guardianList = userMapper.searchguardianList(dto);

        List<Map<String, Object>> result = new ArrayList<>();
        if (doctorList.size() != 0) {
            result.addAll(doctorList);
        }

        if (guardianList.size() != 0) {
            result.addAll(guardianList);
        }

        return result;
    }

    @Override
    public Map<String, Object> ageavgHealthinfo(UserhealthDto dto) {
        return userMapper.ageavgHealthinfo(dto);
    }

}