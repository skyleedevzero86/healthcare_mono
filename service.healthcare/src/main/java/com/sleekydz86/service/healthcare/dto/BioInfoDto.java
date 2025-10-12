package com.sleekydz86.service.healthcare.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@Getter
public class BioInfoDto {
    String heartrate;
    String temperature;
    String stress;
    String bloodpressMax;
    String bloodpressMin;
    String gender;
    String age;
    String userNm;
    String userId;


    public BioInfoDto getBioInfoDto(Map<String, Object> map) {
        this.heartrate = (String) map.get("heartrate");
        this.temperature = (String) map.get("temperature");
        this.stress = (String) map.get("stress");
        this.bloodpressMax = (String) map.get("bloodpressMax");
        this.bloodpressMin = (String) map.get("bloodpressMin");
        this.gender = (String) map.get("gender");
        this.age =(String) map.get("age");
        this.userNm =(String) map.get("userNm");
        this.userId = (String) map.get("userId");

        return this;
    }
}
