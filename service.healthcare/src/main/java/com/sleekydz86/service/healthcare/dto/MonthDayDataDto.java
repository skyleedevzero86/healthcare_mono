package com.sleekydz86.service.healthcare.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class MonthDayDataDto {

    private String userId;

    private String time;

    private String year;

    private String month;

    private String day;

    private String hour;

    private String minute;

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

    private int sleep;

    private int stressMin;

    private int stressMax;

    private int stressAvg;

    private int bloodpressMin;

    private int bloodpressMax;

    private int repiratoryMin;

    private int repiratoryMax;

    private int repiratoryAvg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getYear() {
        return time.substring(0, 4);
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return time.substring(4, 6);
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        if (time.length() > 7) {
            day = time.substring(6, 8);
        } else {
            Date date = new Date();
            day = String.valueOf(date.getDay());
        }
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        LocalDateTime now = LocalDateTime.now();
        hour = String.valueOf(now.getHour());
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        Date date = new Date();
        minute = String.valueOf(date.getMinutes());
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public int getHeartrateMin() {
        return heartrateMin;
    }

    public void setHeartrateMin(int heartrateMin) {
        this.heartrateMin = heartrateMin;
    }

    public int getHeartrateMax() {
        return heartrateMax;
    }

    public void setHeartrateMax(int heartrateMax) {
        this.heartrateMax = heartrateMax;
    }

    public int getHeartrateAvg() {
        return heartrateAvg;
    }

    public void setHeartrateAvg(int heartrateAvg) {
        this.heartrateAvg = heartrateAvg;
    }

    public float getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public float getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public float getTemperatureAvg() {
        return temperatureAvg;
    }

    public void setTemperatureAvg(float temperatureAvg) {
        this.temperatureAvg = temperatureAvg;
    }

    public int getSpo2Min() {
        return spo2Min;
    }

    public void setSpo2Min(int spo2Min) {
        this.spo2Min = spo2Min;
    }

    public int getSpo2Max() {
        return spo2Max;
    }

    public void setSpo2Max(int spo2Max) {
        this.spo2Max = spo2Max;
    }

    public int getSpo2Avg() {
        return spo2Avg;
    }

    public void setSpo2Avg(int spo2Avg) {
        this.spo2Avg = spo2Avg;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStressMin() {
        return stressMin;
    }

    public void setStressMin(int stressMin) {
        this.stressMin = stressMin;
    }

    public int getStressMax() {
        return stressMax;
    }

    public void setStressMax(int stressMax) {
        this.stressMax = stressMax;
    }

    public int getStressAvg() {
        return stressAvg;
    }

    public void setStressAvg(int stressAvg) {
        this.stressAvg = stressAvg;
    }

    public int getBloodpressMin() {
        return bloodpressMin;
    }

    public void setBloodpressMin(int bloodpressMin) {
        this.bloodpressMin = bloodpressMin;
    }

    public int getBloodpressMax() {
        return bloodpressMax;
    }

    public void setBloodpressMax(int bloodpressMax) {
        this.bloodpressMax = bloodpressMax;
    }

    public int getRepiratoryMin() {
        return repiratoryMin;
    }

    public void setRepiratoryMin(int repiratoryMin) {
        this.repiratoryMin = repiratoryMin;
    }

    public int getRepiratoryMax() {
        return repiratoryMax;
    }

    public void setRepiratoryMax(int repiratoryMax) {
        this.repiratoryMax = repiratoryMax;
    }

    public int getRepiratoryAvg() {
        return repiratoryAvg;
    }

    public void setRepiratoryAvg(int repiratoryAvg) {
        this.repiratoryAvg = repiratoryAvg;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

}
