package com.sleekydz86.service.healthcare.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreDto {

    private String userId;
    private String scoreField;
    private double userScore;
    private String date;
}
