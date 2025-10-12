package com.sleekydz86.service.healthcare.dto;

import java.time.LocalDate;

public class MinuteDataDto {

    private String userId;

    private String tid;

    private String time;

    private int heartrate;

    private float temper;

    private int spo2;

    private int step;

    private int stress;

    private int bloodpressMin;

    private int bloodpressMax;

    private int repiratory;

    private int sleep;

    private String year;

    private String month;

    private String day;

    private String hour;

    private String minute;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(int heartrate) {
        this.heartrate = heartrate;
    }

    public float getTemper() {
        return temper;
    }

    public void setTemper(float temper) {
        this.temper = temper;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
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

    public int getRepiratory() {
        return repiratory;
    }

    public void setRepiratory(int repiratory) {
        this.repiratory = repiratory;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public String getYear() {
        LocalDate now = LocalDate.now();
        return String.valueOf(now.getYear());
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return time.substring(0, 2);
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return time.substring(2, 4);
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return time.substring(4, 6);
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return time.substring(6, 8);
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "MinuteDataDto [userId=" + userId + ", tid=" + tid + ", time=" + time + ", heartrate=" + heartrate
                + ", temper=" + temper + ", spo2=" + spo2 + ", step=" + step + ", stress=" + stress
                + ", bloodpressMin=" + bloodpressMin + ", bloodpressMax=" + bloodpressMax + ", repiratory="
                + repiratory + ", sleep=" + sleep + ", year=" + getYear() + ", month=" + getMonth() + ", day=" + getDay()
                + ", hour=" + getHour() + ", minute=" + getMinute() + "]";
    }
}