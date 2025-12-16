package com.sleekydz86.service.usermanagement.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.common.ValidationException;
import com.sleekydz86.service.usermanagement.dto.CamelHashMap;
import com.sleekydz86.service.usermanagement.dto.CommonPagingResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.global.util.PaginationInfo;
import com.sleekydz86.service.usermanagement.global.util.PagingUtil;
import com.sleekydz86.service.usermanagement.metrics.UserManagementMetrics;
import com.sleekydz86.service.usermanagement.repository.UserRepository;
import io.micrometer.core.instrument.Timer;
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
public class UserManagementServiceImpl implements UserManagementService {
    private final UserRepository userRepository;
    private final PagingUtil pagingUtil;
    private final UserManagementMetrics userManagementMetrics;

    @Override
    @Cacheable(value = "userList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
    public ServiceResponse<Object> getUserList(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "userList");
        
        userManagementMetrics.incrementUserListQueries();
        Timer.Sample sample = userManagementMetrics.startUserListQueryTimer();
        
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            log.info("사용자 목록 조회 중: 역할 {}, 페이지 {}", dto.getUserRoleFk(), dto.getPageIdx());
            
            int totalCount = userRepository.countUserList(dto);
            PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

            Object result = CommonPagingResponse.builder()
                    .list(userRepository.findUserList(dto))
                    .totalCount(paginationInfo.getTotalRecordCount())
                    .paginationInfo(paginationInfo)
                    .build();
            
            log.info("사용자 목록 조회 완료: 역할 {}, 전체 개수: {}", dto.getUserRoleFk(), totalCount);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생: 역할 {}", dto.getUserRoleFk(), e);
            return ServiceResponse.error("User list query failed: " + e.getMessage());
        } finally {
            sample.stop(userManagementMetrics.getUserListQueryTime());
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "parentList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
    public ServiceResponse<Object> getParentList(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            int totalCount = userRepository.countParentList(dto);
            PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

            Object result = CommonPagingResponse.builder()
                    .list(userRepository.findParentList(dto))
                    .totalCount(paginationInfo.getTotalRecordCount())
                    .paginationInfo(paginationInfo)
                    .build();
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("Parent list query failed: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "doctorList", key = "#dto.userRoleFk + '_' + #dto.pageIdx + '_' + (#dto.searchKeyword != null ? #dto.searchKeyword : '')")
    public ServiceResponse<Object> getDoctorList(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            int totalCount = userRepository.countDoctorList(dto);
            PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);
            Object result = CommonPagingResponse.builder()
                    .list(userRepository.findDoctorList(dto))
                    .totalCount(paginationInfo.getTotalRecordCount())
                    .paginationInfo(paginationInfo)
                    .build();
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("Doctor list query failed: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "userList", key = "'manage_' + #dto.userId + '_' + #dto.userRoleFk + '_' + #dto.pageIdx")
    public ServiceResponse<Object> getManageUserList(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            int totalCount = userRepository.countManageUserList(dto);
            PaginationInfo paginationInfo = pagingUtil.getPageInfo(dto, totalCount);

            Object result = CommonPagingResponse.builder()
                    .list(userRepository.findManageUserList(dto))
                    .totalCount(paginationInfo.getTotalRecordCount())
                    .paginationInfo(paginationInfo)
                    .build();
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("Manage user list query failed: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "userInfo", key = "#dto.userId + '_' + #dto.userRoleFk")
    public ServiceResponse<Map<String, Object>> getUserInfo(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "userInfo");
        
        userManagementMetrics.incrementUserInfoQueries();
        Timer.Sample sample = userManagementMetrics.startUserInfoQueryTimer();
        
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            log.info("사용자 정보 조회 중: 사용자 {}, 역할 {}", userId, dto.getUserRoleFk());
            
            Map<String, Object> result = userRepository.findUserInfo(dto);
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
            
            log.info("사용자 정보 조회 완료: {}", userId);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생: {}", userId, e);
            return ServiceResponse.error("User info query failed: " + e.getMessage());
        } finally {
            sample.stop(userManagementMetrics.getUserInfoQueryTime());
            MDC.clear();
        }
    }

    @Override
    @CacheEvict(value = {"userInfo", "userList"}, allEntries = true)
    public ServiceResponse<Integer> updateUserInfo(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "updateUserInfo");
        
        userManagementMetrics.incrementUserInfoUpdates();
        Timer.Sample sample = userManagementMetrics.startUserInfoUpdateTimer();
        
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            log.info("사용자 정보 업데이트 중: {}", userId);
            
            int updateResult = userRepository.updateUserInfo(dto);
            
            log.info("사용자 정보 업데이트 완료: 사용자 {}, 결과: {}", userId, updateResult);
            return ServiceResponse.success(updateResult);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("User info update failed: " + e.getMessage());
        } finally {
            sample.stop(userManagementMetrics.getUserInfoUpdateTime());
            MDC.clear();
        }
    }

    @Override
    @CacheEvict(value = {"userInfo", "userList", "parentList", "doctorList"}, allEntries = true)
    public ServiceResponse<Integer> deleteUserInfo(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "deleteUserInfo");
        
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            log.info("사용자 정보 삭제 중: {}", userId);
            
            int result = userRepository.deleteUserInfo(dto);
            
            if (result > 0) {
                userManagementMetrics.incrementUserInfoDeletes();
            }
            
            log.info("사용자 정보 삭제 완료: 사용자 {}, 결과: {}", userId, result);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("사용자 정보 삭제 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("User info delete failed: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @CacheEvict(value = "userInfo", key = "#dto.userId")
    public ServiceResponse<Integer> updatePassword(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        String userId = dto.getUserId() != null ? dto.getUserId() : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "updatePasswd");
        
        try {
            if (dto == null) {
                return ServiceResponse.error("UserDto cannot be null");
            }
            log.info("비밀번호 변경 중: {}", userId);
            
            int result = userRepository.updatePassword(dto);
            
            if (result > 0) {
                userManagementMetrics.incrementPasswordUpdates();
            }
            
            log.info("비밀번호 변경 완료: 사용자 {}, 결과: {}", userId, result);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생: 사용자 {}", userId, e);
            return ServiceResponse.error("Password update failed: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}

