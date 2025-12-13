package com.sleekydz86.service.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.service.ChatService;
import com.sleekydz86.service.healthcare.service.HealthcareService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/healthcare/v1/")
@Validated
public class HealthcareController {

    @Autowired
    HealthcareService healthcareService;
    @Autowired
    Environment env;
    @Autowired
    ChatService chatService;
    @Autowired
    BioInfoDto bioInfoDto;
    @Autowired
    com.sleekydz86.service.healthcare.util.InputSanitizer inputSanitizer;

    private LocalDate getToday() {
        return LocalDate.now();
    }

    @PostMapping("insertHealthInfo")
    public ResponseEntity<ApiResponse> insertHealthInfo(
            HttpServletRequest request,
            @Valid @RequestBody HealthDataRequestDto requestDto) {
        try {
            String sanitizedUserId = inputSanitizer.sanitizeUserId(requestDto.getUserId());
            if (sanitizedUserId == null || !sanitizedUserId.equals(requestDto.getUserId())) {
                return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<>();
            map.put("userId", sanitizedUserId);
            map.put("type", requestDto.getType());
            
            if ("m".equals(requestDto.getType())) {
                for (HealthDataItemDto item : requestDto.getData()) {
                    MinuteDataDto dto = mapper.convertValue(item, MinuteDataDto.class);
                    dto.setUserId(requestDto.getUserId());
                    healthcareService.insMinuteData(dto);
                }
            } else {
                for (HealthDataItemDto item : requestDto.getData()) {
                    MonthDayDataDto dto = mapper.convertValue(item, MonthDayDataDto.class);
                    dto.setUserId(requestDto.getUserId());
                    healthcareService.insMonthDayData(dto);
                    map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    healthcareService.insExerciseScore(map);
                    healthcareService.insStressScore(map);
                }
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            log.error("건강 데이터 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @GetMapping("/health_check")
    public ResponseEntity<ApiResponse> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "healthcare");
        return ApiResponse.ok(result);
    }

    @PostMapping("minmaxHealthInfo")
    public ResponseEntity<ApiResponse> minmaxHealthInfo(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.minmaxHealthInfo(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("healthInfo")
    public ResponseEntity<ApiResponse> healthInfo(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.healthInfo(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("healthInfoChart")
    public ResponseEntity<ApiResponse> healthInfoChart(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            Map<String, Object> result = healthcareService.healthInfoChart(map);
            String[] strArr = (String[]) result.get("lv");
            if (strArr == null || strArr.length == 0) {
                return ApiResponse.error(ApiResultCode.RESULT_IS_EMPTY);
            }
            
            java.util.ArrayList<String[]> lv = new java.util.ArrayList<>();
            String query = (String) map.get("query");
            String[] arr = "Y".equals(query) ? (String[]) result.get("year") : (String[]) result.get("month");
            
            for (int i = 0; i < strArr.length; i++) {
                String year = "";
                if ("Y".equals(query)) {
                    year = "01".equals(strArr[i]) ? arr[i] : "";
                } else {
                    if ("01".equals(arr[i])) {
                        String date = (String) map.get("date");
                        if (date != null && date.length() >= 4) {
                            year = "01".equals(strArr[i]) ? date.substring(0, 4) : "";
                        }
                    }
                    strArr[i] = "01".equals(strArr[i]) ? arr[i] + "/" + strArr[i] : strArr[i];
                }
                String[] res = {strArr[i], year};
                lv.add(res);
            }
            result.put("lv", lv);
            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("customMinuteChart")
    public ResponseEntity<ApiResponse> customMinuteChartData(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.customMinuteChartData(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("분 단위 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("customMinuteDashBRDChart")
    public ResponseEntity<ApiResponse> customMinuteDashBRDChart(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.customMinuteDashBRDChart(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("대시보드 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("dailydata")
    public ResponseEntity<ApiResponse> dailydata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            map.put("todayDate", getToday());
            Map<String, Object> responseData = healthcareService.realtimeBiodata(map);
            Map<String, Object> sleepData = healthcareService.todaySleepdata(map);

            TargetDto dto = new TargetDto();
            if (responseData != null && !responseData.isEmpty()) {
                dto.setCurrentStep(getIntegerValue(responseData.get("step"), 0));
                dto.setCurrentStress(getIntegerValue(responseData.get("stress"), 0));
            } else {
                dto.setCurrentStep(0);
                dto.setCurrentStress(0);
            }

            if (sleepData != null && !sleepData.isEmpty()) {
                Integer sleep = getIntegerValue(sleepData.get("sleep"), 0);
                dto.setTotalSleep(Math.min(sleep, 600));
            } else {
                dto.setTotalSleep(0);
            }

            Map<String, Object> targetData = healthcareService.getTarget(dto);
            responseData.putAll(sleepData);
            responseData.putAll(targetData);

            if (responseData.isEmpty() || sleepData.isEmpty()) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(responseData);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 데이터 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("realtimeBiodata")
    public ResponseEntity<ApiResponse> realtimeBiodata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            map.put("todayDate", getToday());
            Map<String, Object> responseData = healthcareService.realtimeBiodata(map);

            TargetDto dto = new TargetDto();
            if (responseData != null && !responseData.isEmpty()) {
                dto.setCurrentStep(getIntegerValue(responseData.get("step"), 0));
                dto.setCurrentStress(getIntegerValue(responseData.get("stress"), 0));
            } else {
                dto.setCurrentStep(0);
                dto.setCurrentStress(0);
            }

            if (map.get("sleep") != null) {
                try {
                    dto.setTotalSleep(Integer.parseInt(map.get("sleep").toString()));
                } catch (NumberFormatException e) {
                    dto.setTotalSleep(0);
                }
            } else {
                dto.setTotalSleep(0);
            }

            Map<String, Object> targetData = healthcareService.getTarget(dto);
            responseData.putAll(targetData);

            if (responseData.isEmpty()) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(responseData);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("실시간 생체 데이터 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("graphBiodata")
    public ResponseEntity<ApiResponse> graphBiodata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.graphBiodata(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("생체 데이터 그래프 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("healthinfoDailySleep")
    public ResponseEntity<ApiResponse> healthinfoDailySleep(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.healthinfoDailySleep(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 수면 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("insDailyStep")
    public ResponseEntity<ApiResponse> insertDailyStep(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            Map<String, Object> result = new HashMap<>();
            result.put("count", healthcareService.insertDailyStep(map));

            if ((int) result.get("count") == 0) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 걸음수 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("insDailySleep")
    public ResponseEntity<ApiResponse> insertDailySleep(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            Map<String, Object> result = new HashMap<>();
            result.put("count", healthcareService.insertDailySleep(map));

            healthcareService.insSleepScore(map);

            if ((int) result.get("count") == 0) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 수면 정보 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("healthScoreList")
    public ResponseEntity<ApiResponse> healthScoreList(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.healthScoreList(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 점수 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/inscommunity")
    public ResponseEntity<ApiResponse> inscommunity(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.inscommunity(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("커뮤니티 데이터 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("communityList")
    public ResponseEntity<ApiResponse> communityList(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            return ApiResponse.ok(healthcareService.commuList(map));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("커뮤니티 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    @PostMapping("/chat_ai")
    public ResponseEntity<ApiResponse> chat_ai(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", map.get("userId"));

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = today.format(formatter);
            paramMap.put("regDate", formattedDate);
            Map<String, Object> responseMap = healthcareService.getAiResponse(paramMap);
            String aiResponse = "";
            if (responseMap != null) {
                aiResponse = (String) responseMap.get("airesponse");
            } else {
                AIHandleDto aiHandleDto = new AIHandleDto();
                String query = aiHandleDto.getQuery(bioInfoDto.getBioInfoDto(map));

                aiResponse = chatService.getChatResponse(query);

                paramMap.put("aiResponse", aiResponse);
                healthcareService.insAiResponse(paramMap);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("aiResponse", aiResponse);
            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("AI 챗봇 응답 생성 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
        }
    }

    private void validateUserId(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("요청 데이터가 없습니다");
        }
        Object userId = map.get("userId");
        if (userId == null || userId.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        String userIdStr = userId.toString();
        if (userIdStr.length() > 50) {
            throw new IllegalArgumentException("사용자 ID는 50자 이하여야 합니다");
        }
        if (!userIdStr.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("사용자 ID는 영문, 숫자, 언더스코어만 허용됩니다");
        }
        if (inputSanitizer.containsSqlInjection(userIdStr) || inputSanitizer.containsXss(userIdStr)) {
            throw new IllegalArgumentException("사용자 ID에 허용되지 않는 문자가 포함되어 있습니다");
        }
        map.put("userId", inputSanitizer.sanitizeUserId(userIdStr));
    }

    private Integer getIntegerValue(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
