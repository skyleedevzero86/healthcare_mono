package com.sleekydz86.service.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioInfoDto {
    private String userNm;
    private Integer age;
    private Integer heartrate;
    private Integer bloodpressMax;
    private Integer bloodpressMin;
    private Double temperature;
    private Integer stress;
    private Integer oxygenSaturation;
    private Integer steps;

    public static BioInfoDto fromMap(Map<String, Object> map) {
        BioInfoDto dto = new BioInfoDto();
        if (map.get("userNm") != null) {
            dto.setUserNm(map.get("userNm").toString());
        }
        if (map.get("age") != null) {
            dto.setAge(Integer.parseInt(map.get("age").toString()));
        }
        if (map.get("heartrate") != null) {
            dto.setHeartrate(Integer.parseInt(map.get("heartrate").toString()));
        }
        if (map.get("bloodpressMax") != null) {
            dto.setBloodpressMax(Integer.parseInt(map.get("bloodpressMax").toString()));
        }
        if (map.get("bloodpressMin") != null) {
            dto.setBloodpressMin(Integer.parseInt(map.get("bloodpressMin").toString()));
        }
        if (map.get("temperature") != null) {
            dto.setTemperature(Double.parseDouble(map.get("temperature").toString()));
        }
        if (map.get("stress") != null) {
            dto.setStress(Integer.parseInt(map.get("stress").toString()));
        }
        if (map.get("oxygenSaturation") != null) {
            dto.setOxygenSaturation(Integer.parseInt(map.get("oxygenSaturation").toString()));
        }
        if (map.get("steps") != null) {
            dto.setSteps(Integer.parseInt(map.get("steps").toString()));
        }
        return dto;
    }
}
