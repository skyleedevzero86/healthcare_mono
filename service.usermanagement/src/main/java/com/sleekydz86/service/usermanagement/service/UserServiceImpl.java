package com.sleekydz86.service.usermanagement.service;

import com.sleekydz86.service.usermanagement.dto.CamelHashMap;
import com.sleekydz86.service.usermanagement.dto.CommonPagingResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.dto.UserhealthDto;
import com.sleekydz86.service.usermanagement.exception.BusinessException;
import com.sleekydz86.service.usermanagement.global.mapper.UserMapper;
import com.sleekydz86.service.usermanagement.util.DtoConverter;
import com.sleekydz86.service.usermanagement.global.util.PaginationInfo;
import com.sleekydz86.service.usermanagement.global.util.PagingUtil;
import com.sleekydz86.service.usermanagement.global.util.HealthcareEncryptionUtil;
import com.sleekydz86.service.usermanagement.global.util.HealthcareEncryptionUtil.KeyType;
import com.sleekydz86.service.usermanagement.metrics.UserManagementMetrics;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PagingUtil pagingUtil;
    private final UserManagementMetrics userManagementMetrics;
    private final DtoConverter dtoConverter;

    @Cacheable(value = "userList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
    public Object userList(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "userList");

        userManagementMetrics.incrementUserListQueries();
        Timer.Sample sample = userManagementMetrics.startUserListQueryTimer();

        try {
            log.info("사용자 목록 조회 중: 역할 {}, 페이지 {}", dto.getUserRoleFk(), dto.getPageIdx());

            int totalCount = userMapper.userListCount(dto);
            PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

            Object result = CommonPagingResponse.builder()
                    .list(userMapper.userList(dto))
                    .totalCount(paginationInfo.getTotalRecordCount())
                    .paginationInfo(paginationInfo)
                    .build();

            log.info("사용자 목록 조회 완료: 역할 {}, 전체 개수: {}", dto.getUserRoleFk(), totalCount);
            return result;
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생: 역할 {}", dto.getUserRoleFk(), e);
            throw e;
        } finally {
            sample.stop(userManagementMetrics.getUserListQueryTime());
            MDC.clear();
        }
    }

    @Cacheable(value = "parentList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
    public Object parentList(UserDto dto) {
        int totalCount = userMapper.parentListCount(dto);
        PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

        return CommonPagingResponse.builder()
                .list(userMapper.parentList(dto))
                .totalCount(paginationInfo.getTotalRecordCount())
                .paginationInfo(paginationInfo)
                .build();
    }

    @Cacheable(value = "doctorList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
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
    @Cacheable(value = "userList", key = "'manage_' + #dto.userId + '_' + #dto.userRoleFk + '_' + #dto.pageIdx")
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
    @Cacheable(value = "userList", key = "'search_' + #map['searchtype'] + '_' + #map['category'] + '_' + (#map['keyword'] != null ? #map['keyword'] : '')")
    public List<Map<String, Object>> searchuserList(Map<String, Object> map) {
        return userMapper.searchuserList(map);
    }

    @SuppressWarnings("unchecked")
    @Cacheable(value = "userInfo", key = "#dto.userId + '_' + #dto.userRoleFk")
    public Map<String, Object> userInfo(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "userInfo");

        userManagementMetrics.incrementUserInfoQueries();
        Timer.Sample sample = userManagementMetrics.startUserInfoQueryTimer();

        try {
            log.info("사용자 정보 조회 중: 사용자 {}, 역할 {}", userId, dto.getUserRoleFk());

            Map<String, Object> result = userMapper.userInfo(dto);
            ArrayList<CamelHashMap<String, Object>> res = new ArrayList<CamelHashMap<String, Object>>();
            if (result != null) {
                if (result.containsKey("guardian")) {
                    if (result.get("guardian") != null && !result.get("guardian").equals("")) {
                        String str = (String) result.get("guardian");
                        ArrayList<Map<String, Object>> arr = dtoConverter.fromJson(str, ArrayList.class);
                        for (Map<String, Object> s : arr) {
                            CamelHashMap<String, Object> cm = dtoConverter.convertToEntity(s, CamelHashMap.class);
                            res.add(cm);
                        }
                        result.put("guardian", res);
                    }
                }

                if (result.get("birthEnc") != null) {
                    result.put("birthDecrypted", HealthcareEncryptionUtil.decrypt(
                            (String) result.get("birthEnc"),
                            KeyType.USER));
                }
                if (result.get("telNumEnc") != null) {
                    result.put("telNumDecrypted", HealthcareEncryptionUtil.decrypt(
                            (String) result.get("telNumEnc"),
                            KeyType.USER));
                }
            }

            log.info("사용자 정보 조회 완료: {}", userId);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생: {}", userId, e);
            throw new BusinessException(
                    "사용자 정보 조회 중 오류가 발생했습니다.",
                    e,
                    com.sleekydz86.service.usermanagement.dto.ApiResultCode.UNKNOWN_ERR);
        } finally {
            sample.stop(userManagementMetrics.getUserInfoQueryTime());
            MDC.clear();
        }
    }

    @CacheEvict(value = { "userInfo", "userList" }, allEntries = true)
    public int updateUserInfo(@Valid UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "updateUserInfo");

        userManagementMetrics.incrementUserInfoUpdates();
        Timer.Sample sample = userManagementMetrics.startUserInfoUpdateTimer();

        try {
            log.info("사용자 정보 업데이트 중: {}", userId);

            if (dto.getBirthEnc() != null) {
                dto.setBirthEnc(HealthcareEncryptionUtil.encrypt(dto.getBirthEnc(), KeyType.USER));
            }
            if (dto.getTelNumEnc() != null) {
                dto.setTelNumEnc(HealthcareEncryptionUtil.encrypt(dto.getTelNumEnc(), KeyType.USER));
            }

            ArrayList<Map<String, Object>> result = new ArrayList<>();
            String str = dto.getGuardian();
            try {

                int cnt = userMapper.searchParentCount(dto);
                ArrayList<Integer> arr = new ArrayList<Integer>();
                if (str != null) {
                    result = dtoConverter.fromJson(str, ArrayList.class);
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

            } catch (IllegalArgumentException e) {
                log.error("JSON 처리 중 오류 발생: 사용자 {}", userId, e);
            }
            if (dto.getDoctorSeq() != 0 && dto.getDoctorId() != null && !dto.getDoctorId().equals("")) {
                userMapper.updateDoctorMaping(dto);
            }

            int updateResult = userMapper.updateUserInfo(dto);

            log.info("사용자 정보 업데이트 완료: 사용자 {}, 결과: {}", userId, updateResult);
            return updateResult;
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            sample.stop(userManagementMetrics.getUserInfoUpdateTime());
            MDC.clear();
        }
    }

    @CacheEvict(value = { "userInfo", "userList", "parentList", "doctorList" }, allEntries = true)
    public int deleteUserInfo(@Valid UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "deleteUserInfo");

        try {
            log.info("사용자 정보 삭제 중: {}", userId);

            int result = userMapper.deleteUserInfo(dto);

            if (result > 0) {
                userManagementMetrics.incrementUserInfoDeletes();
            }

            log.info("사용자 정보 삭제 완료: 사용자 {}, 결과: {}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("사용자 정보 삭제 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @CacheEvict(value = "userInfo", key = "#dto.userId")
    public int updatePasswd(@Valid UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "updatePasswd");

        try {
            log.info("비밀번호 변경 중: {}", userId);

            int result = userMapper.updatePasswd(dto);

            if (result > 0) {
                userManagementMetrics.incrementPasswordUpdates();
            }

            log.info("비밀번호 변경 완료: 사용자 {}, 결과: {}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Cacheable(value = "userList", key = "'doctor_' + #dto.userNm")
    public List<Map<String, Object>> searchDoctor(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "searchDoctor");

        try {
            log.info("의사 검색 중: 검색어 {}", dto.getUserNm());

            List<Map<String, Object>> result = userMapper.searchDoctor(dto);

            userManagementMetrics.incrementDoctorSearches();

            log.info("의사 검색 완료, 결과 개수: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("의사 검색 중 오류 발생", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Cacheable(value = "userList", key = "'parent_' + #dto.userNm + '_' + #dto.birthEnc + '_' + #dto.telNumEnc")
    public List<Map<String, Object>> searchParent(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "searchParent");

        try {
            log.info("보호자 검색 중: 검색어 {}", dto.getUserNm());

            List<Map<String, Object>> result = userMapper.searchParent(dto);

            userManagementMetrics.incrementParentSearches();

            log.info("보호자 검색 완료, 결과 개수: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("보호자 검색 중 오류 발생", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Cacheable(value = "userList", key = "'health_' + #map['userRoleFk'] + '_' + (#map['searchKeyword'] != null ? #map['searchKeyword'] : '')")
    public List<Map<String, Object>> searchHealthUserList(Map<String, Object> map) {
        UserDto dto = dtoConverter.convertFromMap(map, UserDto.class);
        return userMapper.searchHealthUserList(dto);
    }

    @Override
    @Cacheable(value = "userList", key = "'drguardian_' + #dto.userId")
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
    @Cacheable(value = "healthData", key = "'ageavg_' + #dto.ageRange + '_' + #dto.gender")
    public Map<String, Object> ageavgHealthinfo(UserhealthDto dto) {
        return userMapper.ageavgHealthinfo(dto);
    }

}