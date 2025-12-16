package com.sleekydz86.service.usermanagement.service.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;
import com.sleekydz86.service.usermanagement.metrics.UserManagementMetrics;
import com.sleekydz86.service.usermanagement.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {
    private final UserSearchRepository userSearchRepository;
    private final SearchCriteriaBuilder searchCriteriaBuilder;
    private final UserManagementMetrics userManagementMetrics;

    @Override
    @Cacheable(value = "userList", key = "'search_' + #map['searchtype'] + '_' + #map['category'] + '_' + (#map['keyword'] != null ? #map['keyword'] : '')")
    public ServiceResponse<List<Map<String, Object>>> searchUserList(Map<String, Object> map) {
        try {
            if (map == null) {
                return ServiceResponse.error("검색 파라미터는 null일 수 없습니다");
            }
            List<Map<String, Object>> result = userSearchRepository.searchUserList(map);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("사용자 검색 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "userList", key = "'doctor_' + #dto.userNm")
    public ServiceResponse<List<Map<String, Object>>> searchDoctor(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "searchDoctor");
        
        try {
            if (dto == null) {
                return ServiceResponse.error("사용자 정보는 null일 수 없습니다");
            }
            log.info("의사 검색 중: 검색어 {}", dto.getUserNm());
            
            List<Map<String, Object>> result = userSearchRepository.searchDoctor(dto);
            
            userManagementMetrics.incrementDoctorSearches();
            
            log.info("의사 검색 완료, 결과 개수: {}", result != null ? result.size() : 0);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("의사 검색 중 오류 발생", e);
            return ServiceResponse.error("의사 검색 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "userList", key = "'parent_' + #dto.userNm + '_' + #dto.birthEnc + '_' + #dto.telNumEnc")
    public ServiceResponse<List<Map<String, Object>>> searchParent(UserDto dto) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "searchParent");
        
        try {
            if (dto == null) {
                return ServiceResponse.error("사용자 정보는 null일 수 없습니다");
            }
            log.info("보호자 검색 중: 검색어 {}", dto.getUserNm());
            
            List<Map<String, Object>> result = userSearchRepository.searchParent(dto);
            
            userManagementMetrics.incrementParentSearches();
            
            log.info("보호자 검색 완료, 결과 개수: {}", result != null ? result.size() : 0);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            log.error("보호자 검색 중 오류 발생", e);
            return ServiceResponse.error("보호자 검색 실패: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "userList", key = "'health_' + #map['userRoleFk'] + '_' + (#map['searchKeyword'] != null ? #map['searchKeyword'] : '')")
    public ServiceResponse<List<Map<String, Object>>> searchHealthUserList(Map<String, Object> map) {
        try {
            if (map == null) {
                return ServiceResponse.error("검색 파라미터는 null일 수 없습니다");
            }
            UserDto dto = searchCriteriaBuilder.buildSearchCriteria(map);
            List<Map<String, Object>> result = userSearchRepository.searchHealthUserList(dto);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("건강 사용자 검색 실패: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "userList", key = "'drguardian_' + #dto.userId")
    public ServiceResponse<List<Map<String, Object>>> searchDoctorGuardianList(UserDto dto) {
        try {
            if (dto == null) {
                return ServiceResponse.error("사용자 정보는 null일 수 없습니다");
            }
            List<Map<String, Object>> result = userSearchRepository.searchDoctorGuardianList(dto);
            return ServiceResponse.success(result);
        } catch (Exception e) {
            return ServiceResponse.error("의사/보호자 검색 실패: " + e.getMessage());
        }
    }
}

