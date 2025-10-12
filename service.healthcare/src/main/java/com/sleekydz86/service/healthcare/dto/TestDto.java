package com.sleekydz86.service.healthcare.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
public class TestDto {

    private String userId;

    private String time;

    private int heartrate;

    private int heartrateMin;

    private int heartrateMax;

    private int heartrateAvg;

    private float temperature;

    private float temperatureMin;

    private float temperatureMax;

    private float temperatureAvg;

    private int spo2;

    private int spo2Min;

    private int spo2Max;

    private int spo2Avg;

    private int step;

    private int stress;

    private int stressMin;

    private int stressMax;

    private int stressAvg;

    private int bloodpressMin;

    private int bloodpressMax;

    private int bloodpressAvg;

    private int repiratory;

    private int repiratoryMin;

    private int repiratoryMax;

    private int repiratoryAvg;

    private int sleep;

    private String week;

    private Timestamp lastTime;

    private Timestamp sendTime;

    private Timestamp receiveTime;

    private String year;

    private String month;

    private String day;

    private String hour;

    private String minute;

    private String date;
}
