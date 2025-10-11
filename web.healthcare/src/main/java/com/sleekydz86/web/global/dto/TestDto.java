package com.sleekydz86.web.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDto {
    String userId;
    String cate;

    String text;
    int score;

    public TestDto(String userId, String cate, String text, int score) {
        this.userId = userId;
        this.cate = cate;
        this.text = text;
        this.score = score;
    }

}
