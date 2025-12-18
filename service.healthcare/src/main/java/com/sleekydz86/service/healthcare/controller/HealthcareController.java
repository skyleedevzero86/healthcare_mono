package com.sleekydz86.service.healthcare.controller;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.service.ChatService;
import com.sleekydz86.service.healthcare.service.MedicalRecordService;
import com.sleekydz86.service.healthcare.service.PatientService;
import com.sleekydz86.service.healthcare.service.ai.AIResponseService;
import com.sleekydz86.service.healthcare.service.cache.CacheService;
import com.sleekydz86.service.healthcare.service.chart.ChartDataService;
import com.sleekydz86.service.healthcare.service.community.CommunityService;
import com.sleekydz86.service.healthcare.service.healthdata.HealthDataService;
import com.sleekydz86.service.healthcare.service.score.HealthScoreService;
import com.sleekydz86.service.healthcare.util.DtoConverter;
import com.sleekydz86.service.healthcare.util.SqlInjectionValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/healthcare/v1/")
@RequiredArgsConstructor
@Validated
public class HealthcareController {

    private final HealthDataService healthDataService;
    private final ChartDataService chartDataService;
    private final HealthScoreService healthScoreService;
    private final AIResponseService aiResponseService;
    private final CommunityService communityService;
    private final Environment env;
    private final ChatService chatService;
    private final BioInfoDto bioInfoDto;
    private final com.sleekydz86.service.healthcare.util.InputSanitizer inputSanitizer;
    private final PatientService patientService;
    private final MedicalRecordService medicalRecordService;
    private final CacheService cacheService;
    private final DtoConverter dtoConverter;

    public HealthcareController(HealthDataService healthDataService,
            ChartDataService chartDataService,
            HealthScoreService healthScoreService,
            AIResponseService aiResponseService,
            CommunityService communityService,
            Environment env,
            ChatService chatService,
            BioInfoDto bioInfoDto,
            com.sleekydz86.service.healthcare.util.InputSanitizer inputSanitizer,
            PatientService patientService,
            MedicalRecordService medicalRecordService,
            CacheService cacheService) {
        this.healthDataService = healthDataService;
        this.chartDataService = chartDataService;
        this.healthScoreService = healthScoreService;
        this.aiResponseService = aiResponseService;
        this.communityService = communityService;
        this.env = env;
        this.chatService = chatService;
        this.bioInfoDto = bioInfoDto;
        this.inputSanitizer = inputSanitizer;
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
        this.cacheService = cacheService;
    }

    private LocalDate getToday() {
        return LocalDate.now();
    }

    @PostMapping("/healthdata/minute")
    public ResponseEntity<ServiceResponse<Integer>> insertMinuteData(@RequestBody MinuteDataDto dto) {
        ServiceResponse<Integer> response = healthDataService.insertMinuteData(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/healthdata/info")
    public ResponseEntity<ServiceResponse<List<HealthData>>> getHealthInfo(@RequestParam Map<String, Object> params) {
        ServiceResponse<List<HealthData>> response = healthDataService.getHealthInfo(params);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart/healthinfo")
    public ResponseEntity<ServiceResponse<Map<String, Object>>> getHealthInfoChart(
            @RequestParam Map<String, Object> params) {
        ServiceResponse<Map<String, Object>> response = chartDataService.getHealthInfoChart(params);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/score/sleep")
    public ResponseEntity<ServiceResponse<Integer>> calculateSleepScore(@RequestBody Map<String, Object> params) {
        ServiceResponse<Integer> response = healthScoreService.calculateSleepScore(params);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai/response")
    public ResponseEntity<ServiceResponse<Map<String, Object>>> getAIResponse(
            @RequestParam Map<String, Object> params) {
        ServiceResponse<Map<String, Object>> response = aiResponseService.getAIResponse(params);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/community/post")
    public ResponseEntity<ServiceResponse<Integer>> createPost(@RequestBody Map<String, Object> params) {
        ServiceResponse<Integer> response = communityService.createPost(params);
        return ResponseEntity.ok(response);
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

            Map<String, Object> map = new HashMap<>();
            map.put("userId", sanitizedUserId);
            map.put("type", requestDto.getType());

            if ("m".equals(requestDto.getType())) {
                for (HealthDataItemDto item : requestDto.getData()) {
                    MinuteDataDto dto = dtoConverter.convertToEntity(item, MinuteDataDto.class);
                    dto.setUserId(requestDto.getUserId());
                    ServiceResponse<Integer> response = healthDataService.insertMinuteData(dto);
                    if (!response.isSuccess()) {
                        return convertToApiResponse(response);
                    }
                }
            } else {
                for (HealthDataItemDto item : requestDto.getData()) {
                    MonthDayDataDto dto = dtoConverter.convertToEntity(item, MonthDayDataDto.class);
                    dto.setUserId(requestDto.getUserId());
                    ServiceResponse<Integer> response = healthDataService.insertMonthDayData(dto);
                    if (!response.isSuccess()) {
                        return convertToApiResponse(response);
                    }
                    map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    ServiceResponse<Integer> exerciseResponse = healthScoreService.calculateExerciseScore(map);
                    if (!exerciseResponse.isSuccess()) {
                        return convertToApiResponse(exerciseResponse);
                    }
                    ServiceResponse<Integer> stressResponse = healthScoreService.calculateStressScore(map);
                    if (!stressResponse.isSuccess()) {
                        return convertToApiResponse(stressResponse);
                    }
                }
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            log.error("건강 데이터 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
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
            if (map.containsKey("searchWrd") && map.get("searchWrd") != null) {
                String searchWrd = map.get("searchWrd").toString();
                SqlInjectionValidator.sanitizeColumnName(searchWrd);
                map.put("searchWrd", SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            ServiceResponse response = healthDataService.getMinMaxHealthInfo(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("healthInfo")
    public ResponseEntity<ApiResponse> healthInfo(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            if (map.containsKey("searchWrd") && map.get("searchWrd") != null) {
                String searchWrd = map.get("searchWrd").toString();
                map.put("searchWrd", SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            ServiceResponse response = healthDataService.getHealthInfo(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("healthInfoChart")
    public ResponseEntity<ApiResponse> healthInfoChart(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            if (map.containsKey("searchWrd") && map.get("searchWrd") != null) {
                String searchWrd = map.get("searchWrd").toString();
                map.put("searchWrd", SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            if (map.containsKey("condition") && map.get("condition") != null) {
                String condition = map.get("condition").toString();
                map.put("condition", SqlInjectionValidator.sanitizeCondition(condition));
            }
            ServiceResponse<Map<String, Object>> response = chartDataService.getHealthInfoChart(map);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            Map<String, Object> result = response.getData();
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
                String[] res = { strArr[i], year };
                lv.add(res);
            }
            result.put("lv", lv);
            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 정보 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("customMinuteChart")
    public ResponseEntity<ApiResponse> customMinuteChartData(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            if (map.containsKey("searchWrd") && map.get("searchWrd") != null) {
                String searchWrd = map.get("searchWrd").toString();
                map.put("searchWrd", SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            if (map.containsKey("condition") && map.get("condition") != null) {
                String condition = map.get("condition").toString();
                map.put("condition", SqlInjectionValidator.sanitizeCondition(condition));
            }
            ServiceResponse response = chartDataService.getCustomMinuteChartData(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("분 단위 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("customMinuteDashBRDChart")
    public ResponseEntity<ApiResponse> customMinuteDashBRDChart(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            if (map.containsKey("searchWrd") && map.get("searchWrd") != null) {
                String searchWrd = map.get("searchWrd").toString();
                map.put("searchWrd", SqlInjectionValidator.sanitizeColumnName(searchWrd));
            }
            if (map.containsKey("condition") && map.get("condition") != null) {
                String condition = map.get("condition").toString();
                map.put("condition", SqlInjectionValidator.sanitizeCondition(condition));
            }
            ServiceResponse response = chartDataService.getCustomMinuteDashBRDChart(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("대시보드 차트 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("dailydata")
    public ResponseEntity<ApiResponse> dailydata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            map.put("todayDate", getToday());
            ServiceResponse<Map<String, Object>> responseDataResponse = chartDataService.getRealtimeBiodata(map);
            if (!responseDataResponse.isSuccess()) {
                return convertToApiResponse(responseDataResponse);
            }
            Map<String, Object> responseData = responseDataResponse.getData();

            ServiceResponse<Map<String, Object>> sleepDataResponse = chartDataService.getTodaySleepdata(map);
            if (!sleepDataResponse.isSuccess()) {
                return convertToApiResponse(sleepDataResponse);
            }
            Map<String, Object> sleepData = sleepDataResponse.getData();

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

            ServiceResponse<Map<String, Object>> targetResponse = healthScoreService.getTarget(dto);
            if (!targetResponse.isSuccess()) {
                return convertToApiResponse(targetResponse);
            }
            Map<String, Object> targetData = targetResponse.getData();
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
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("realtimeBiodata")
    public ResponseEntity<ApiResponse> realtimeBiodata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            map.put("todayDate", getToday());
            ServiceResponse<Map<String, Object>> responseDataResponse = chartDataService.getRealtimeBiodata(map);
            if (!responseDataResponse.isSuccess()) {
                return convertToApiResponse(responseDataResponse);
            }
            Map<String, Object> responseData = responseDataResponse.getData();

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

            ServiceResponse<Map<String, Object>> targetResponse = healthScoreService.getTarget(dto);
            if (!targetResponse.isSuccess()) {
                return convertToApiResponse(targetResponse);
            }
            Map<String, Object> targetData = targetResponse.getData();
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
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("graphBiodata")
    public ResponseEntity<ApiResponse> graphBiodata(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse response = chartDataService.getGraphBiodata(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("생체 데이터 그래프 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("healthinfoDailySleep")
    public ResponseEntity<ApiResponse> healthinfoDailySleep(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse response = chartDataService.getHealthinfoDailySleep(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 수면 정보 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("insDailyStep")
    public ResponseEntity<ApiResponse> insertDailyStep(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse<Integer> response = healthDataService.insertDailyStep(map);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("count", response.getData());

            if (response.getData() == null || response.getData() == 0) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 걸음수 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("insDailySleep")
    public ResponseEntity<ApiResponse> insertDailySleep(
            HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse<Integer> response = healthDataService.insertDailySleep(map);
            if (!response.isSuccess()) {
                return convertToApiResponse(response);
            }

            ServiceResponse<Integer> sleepScoreResponse = healthScoreService.calculateSleepScore(map);
            if (!sleepScoreResponse.isSuccess()) {
                return convertToApiResponse(sleepScoreResponse);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("count", response.getData());

            if (response.getData() == null || response.getData() == 0) {
                return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
            }

            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("일일 수면 정보 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("healthScoreList")
    public ResponseEntity<ApiResponse> healthScoreList(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse response = healthScoreService.getHealthScoreList(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("건강 점수 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("/inscommunity")
    public ResponseEntity<ApiResponse> inscommunity(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse response = communityService.createPost(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("커뮤니티 데이터 저장 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
        }
    }

    @PostMapping("communityList")
    public ResponseEntity<ApiResponse> communityList(
            @Valid @RequestBody Map<String, Object> map) {
        try {
            validateUserId(map);
            ServiceResponse response = communityService.getPostList(map);
            return convertToApiResponse(response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("커뮤니티 목록 조회 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
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
            ServiceResponse<Map<String, Object>> aiResponseResponse = aiResponseService.getAIResponse(paramMap);
            String aiResponse = "";
            if (aiResponseResponse.isSuccess() && aiResponseResponse.getData() != null) {
                Map<String, Object> responseMap = aiResponseResponse.getData();
                aiResponse = (String) responseMap.get("airesponse");
            }

            if (aiResponse == null || aiResponse.isEmpty()) {
                AIHandleDto aiHandleDto = new AIHandleDto();
                String query = aiHandleDto.getQuery(bioInfoDto.getBioInfoDto(map));

                aiResponse = chatService.getChatResponse(query);

                paramMap.put("aiResponse", aiResponse);
                ServiceResponse<Integer> saveResponse = aiResponseService.saveAIResponse(paramMap);
                if (!saveResponse.isSuccess()) {
                    return convertToApiResponse(saveResponse);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("aiResponse", aiResponse);
            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("AI 챗봇 응답 생성 중 오류 발생", e);
            return ApiResponse.error(ApiResultCode.UNKNOWN_ERR);
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

    @GetMapping("/api/healthcare/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = cacheService.getPatient(id);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient);
    }

    @PostMapping("/api/healthcare/patients")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = patientService.createPatient(patient);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/api/healthcare/patients/{patientId}/medical-records")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = cacheService.getMedicalRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/api/healthcare/patients/{patientId}/medical-records")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@PathVariable Long patientId,
            @RequestBody MedicalRecord record) {
        record.setPatientId(patientId);
        MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(record);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdRecord);
    }

    private <T> ResponseEntity<ApiResponse<T>> convertToApiResponse(ServiceResponse<T> serviceResponse) {
        if (serviceResponse.isSuccess()) {
            return ApiResponse.ok(serviceResponse.getData());
        } else {
            ApiResultCode errorCode = ApiResultCode.UNKNOWN_ERR;
            if ("400".equals(serviceResponse.getResultCode())) {
                errorCode = ApiResultCode.PARAM_VALID_ERR;
            } else if ("500".equals(serviceResponse.getResultCode())) {
                errorCode = ApiResultCode.UNKNOWN_ERR;
            }
            return ApiResponse.error(errorCode, serviceResponse.getMessage());
        }
    }
}
