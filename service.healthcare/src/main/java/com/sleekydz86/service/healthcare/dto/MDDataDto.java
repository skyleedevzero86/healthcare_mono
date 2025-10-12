package com.sleekydz86.service.healthcare.dto;

import lombok.Data;

@Data
public class MDDataDto {
    private String userId;
    private int heartrateMin;
    private int heartrateMax;
    private int heartrateAvg;
    private float temperatureMin;
    private float temperatureMax;
    private float temperatureAvg;
    private int spo2Min;
    private int spo2Max;
    private int spo2Avg;
    private int step;
    private int stressMin;
    private int stressMax;
    private int stressAvg;
    private int bloodpressMin;
    private int bloodpressMax;
    private int repiratoryMin;
    private int repiratoryMax;
    private int repiratoryAvg;
    private int sleep;
    private String time;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
}