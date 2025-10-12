package com.sleekydz86.service.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.healthcare.dto.*;
import com.sleekydz86.service.healthcare.service.ChatService;
import com.sleekydz86.service.healthcare.service.HealthcareService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/healthcare/v1/")
public class HealthcareController {

    @Autowired
    HealthcareService healthcareService;
    @Autowired
    Environment env;

    @Autowired
    ChatService chatService;

    @Autowired
    BioInfoDto bioInfoDto;

    private LocalDate getToday() {
        return LocalDate.now();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("insertHealthInfo")
    public ResponseEntity<ApiResponse> insertHealthInfo(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        if (map.get("type").equals("m")) {
            for (Map<String, Object> obj : (ArrayList<Map<String, Object>>) map.get("data")) {
                CamelHashMap<String, Object> cm = mapper.convertValue(obj, CamelHashMap.class);
                MinuteDataDto dto = mapper.convertValue(cm, MinuteDataDto.class);
                dto.setUserId((String) map.get("userId"));
                healthcareService.insMinuteData(dto);
            }
        } else {
            for (Map<String, Object> obj : (ArrayList<Map<String, Object>>) map.get("data")) {
                CamelHashMap<String, Object> cm = mapper.convertValue(obj, CamelHashMap.class);
                MonthDayDataDto dto = mapper.convertValue(cm, MonthDayDataDto.class);
                dto.setUserId((String) map.get("userId"));
                healthcareService.insMonthDayData(dto);
                UserDateDto userDateDto = mapper.convertValue(dto, UserDateDto.class);
                healthcareService.insExerciseScore(map);
                healthcareService.insStressScore(map);
            }
        }
        return ApiResponse.ok();
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("it working in health Service"
                + ", db usernamae" + env.getProperty("spring.datasource.url"));
    }

    @PostMapping("minmaxHealthInfo")
    public ResponseEntity<ApiResponse> minmaxHealthInfo(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.minmaxHealthInfo(map));
    }

    @PostMapping("healthInfo")
    public ResponseEntity<ApiResponse> healthInfo(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.healthInfo(map));
    }

    @PostMapping("healthInfoChart")
    public ResponseEntity<ApiResponse> healthInfoChart(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        Map<String, Object> result = healthcareService.healthInfoChart(map);
        String[] strArr = (String[]) result.get("lv");
        ArrayList<String[]> lv = new ArrayList<String[]>();
        String[] arr = map.get("query").equals("Y") ? (String[]) result.get("year") : (String[]) result.get("month");
        for (int i = 0; i < strArr.length; i++) {
            String year = "";
            if (map.get("query").equals("Y")) {
                year = strArr[i].equals("01") ? arr[i] : "";
            } else {
                if (arr[i].equals("01")) {
                    String date = (String) map.get("date");
                    year = strArr[i].equals("01") ? date.substring(0, 4) : "";
                }
                strArr[i] = strArr[i].equals("01") ? arr[i] + "/" + strArr[i] : strArr[i];
            }
            String[] res = { strArr[i], year };
            lv.add(res);
        }
        result.put("lv", lv);
        return ApiResponse.ok(result);
    }

    @PostMapping("customMinuteChart")
    public ResponseEntity<ApiResponse> customMinuteChartData(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.customMinuteChartData(map));
    }

    @PostMapping("customMinuteDashBRDChart")
    public ResponseEntity<ApiResponse> customMinuteDashBRDChart(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.customMinuteDashBRDChart(map));
    }

    @PostMapping("dailydata")
    public ResponseEntity<ApiResponse> dailydata(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        map.put("todayDate", getToday());
        Map<String, Object> responseData = healthcareService.realtimeBiodata(map);
        Map<String, Object> sleepData = healthcareService.todaySleepdata(map);
        log.info("ash dailydata : " + sleepData.toString());

        TargetDto dto = new TargetDto();
        if (responseData != null && !responseData.isEmpty()) {
            log.info("ash dailydata : " + responseData.toString());
            dto.setCurrentStep((Integer) responseData.get("step"));
            dto.setCurrentStress((Integer) responseData.get("stress"));
        } else {
            dto.setCurrentStep(0);
            dto.setCurrentStress(0);
        }

        if (sleepData != null && !sleepData.isEmpty()) {
            if ((Integer) sleepData.get("sleep") > 600) {
                dto.setTotalSleep(600);
            } else {
                dto.setTotalSleep((Integer) sleepData.get("sleep"));
            }
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
    }

    @PostMapping("realtimeBiodata")
    public ResponseEntity<ApiResponse> realtimeBiodata(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        map.put("todayDate", getToday());
        Map<String, Object> responseData = healthcareService.realtimeBiodata(map);

        TargetDto dto = new TargetDto();
        if (responseData != null && !responseData.isEmpty()) {
            dto.setCurrentStep((Integer) responseData.get("step"));
            dto.setCurrentStress((Integer) responseData.get("stress"));
        } else {
            dto.setCurrentStep(0);
            dto.setCurrentStress(0);
        }

        if (map.get("sleep") != null) {
            dto.setTotalSleep(Integer.parseInt((String) map.get("sleep")));
        } else {
            dto.setTotalSleep(0);
        }

        Map<String, Object> targetData = healthcareService.getTarget(dto);
        responseData.putAll(targetData);

        if (responseData.isEmpty()) {
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);
        }

        return ApiResponse.ok(responseData);
    }

    @PostMapping("graphBiodata")
    public ResponseEntity<ApiResponse> graphBiodata(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        log.info((String) map.get("startTime"));
        return ApiResponse.ok(healthcareService.graphBiodata(map));
    }

    @PostMapping("healthinfoDailySleep")
    public ResponseEntity<ApiResponse> healthinfoDailySleep(@RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.healthinfoDailySleep(map));
    }

    @PostMapping("insDailyStep")
    public ResponseEntity<ApiResponse> insertDailyStep(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", healthcareService.insertDailyStep(map));

        if ((int) result.get("count") == 0)
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);

        return ApiResponse.ok(result);
    }

    @PostMapping("insDailySleep")
    public ResponseEntity<ApiResponse> insertDailySleep(HttpServletRequest request,
            @RequestBody Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", healthcareService.insertDailySleep(map));

        healthcareService.insSleepScore(map);

        if ((int) result.get("count") == 0)
            return ApiResponse.error(ApiResultCode.UPDATE_FAIL);

        return ApiResponse.ok(result);
    }

    @PostMapping("healthScoreList")
    public ResponseEntity<ApiResponse> healthScoreList(@RequestBody Map<String, Object> map) {
        log.info("ash healthinfo + " + map.get("userId"));
        return ApiResponse.ok(healthcareService.healthScoreList(map));
    }

    @PostMapping("/inscommunity")
    public ResponseEntity<ApiResponse> inscommunity(@RequestBody Map<String, Object> map) {
        return ApiResponse.ok(healthcareService.inscommunity(map));
    }

    @PostMapping("communityList")
    public ResponseEntity<ApiResponse> communityList(@RequestBody Map<String, Object> map) {
        log.info("ash result" + map.toString());
        return ApiResponse.ok(healthcareService.commuList(map));
    }

    @PostMapping("/chat_ai")
    public ResponseEntity<ApiResponse> chat_ai(@RequestBody Map<String, Object> map) {

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
    }
}