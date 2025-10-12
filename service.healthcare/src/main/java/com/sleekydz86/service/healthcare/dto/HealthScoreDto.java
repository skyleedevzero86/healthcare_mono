package com.sleekydz86.service.healthcare.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HealthScoreDto {

    private String userId;
    private Date dailyDate;

    private int userSleepScore;
    private int userExerciseScore;
    private int userStressScore;
    private int healthScore;

}
