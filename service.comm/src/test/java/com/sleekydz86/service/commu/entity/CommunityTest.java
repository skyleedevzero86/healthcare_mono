package com.sleekydz86.service.commu.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Community Entity 테스트")
class CommunityTest {

    private Community community;

    @BeforeEach
    void setUp() {
        community = new Community();
    }

    @Test
    @DisplayName("Community 객체 생성 테스트")
    void createCommunity() {
        assertThat(community).isNotNull();
        assertThat(community.getCommuSeq()).isEqualTo(0);
        assertThat(community.getContent()).isNull();
        assertThat(community.getRegDate()).isNull();
    }

    @Test
    @DisplayName("게시글 내용 설정 및 조회 테스트")
    void setAndGetContent() {
        String content = "테스트 게시글 내용입니다.";

        community.setContent(content);

        assertThat(community.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("사용자명 설정 및 조회 테스트")
    void setAndGetUserNm() {
        String userNm = "testUser";

        community.setUserNm(userNm);

        assertThat(community.getUserNm()).isEqualTo(userNm);
    }

    @Test
    @DisplayName("등록일 설정 및 조회 테스트")
    void setAndGetRegDate() {
        Date regDate = new Date();

        community.setRegDate(regDate);

        assertThat(community.getRegDate()).isEqualTo(regDate);
    }

    @Test
    @DisplayName("사용자 ID 설정 및 조회 테스트")
    void setAndGetUserId() {
        String userId = "123";

        community.setUserId(userId);

        assertThat(community.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("심박수 설정 및 조회 테스트")
    void setAndGetHeartrate() {
        int heartrate = 80;

        community.setHeartrate(heartrate);

        assertThat(community.getHeartrate()).isEqualTo(heartrate);
    }

    @Test
    @DisplayName("체온 설정 및 조회 테스트")
    void setAndGetTemperature() {
        double temperature = 36.5;

        community.setTemperature(temperature);

        assertThat(community.getTemperature()).isEqualTo(temperature);
    }

    @Test
    @DisplayName("혈압 설정 및 조회 테스트")
    void setAndGetBloodpress() {
        double bloodpress = 120.0;

        community.setBloodpress(bloodpress);

        assertThat(community.getBloodpress()).isEqualTo(bloodpress);
    }

    @Test
    @DisplayName("흡연 여부 설정 및 조회 테스트")
    void setAndGetSmoking() {
        int smoking = 1;

        community.setSmoking(smoking);

        assertThat(community.getSmoking()).isEqualTo(smoking);
    }

    @Test
    @DisplayName("음주 여부 설정 및 조회 테스트")
    void setAndGetDrinking() {
        int drinking = 0;

        community.setDrinking(drinking);

        assertThat(community.getDrinking()).isEqualTo(drinking);
    }

    @Test
    @DisplayName("운동량 설정 및 조회 테스트")
    void setAndGetExercise() {
        int exercise = 30;

        community.setExercise(exercise);

        assertThat(community.getExercise()).isEqualTo(exercise);
    }

    @Test
    @DisplayName("나이 설정 및 조회 테스트")
    void setAndGetAge() {
        int age = 30;

        community.setAge(age);

        assertThat(community.getAge()).isEqualTo(age);
    }

    @Test
    @DisplayName("체력 나이 설정 및 조회 테스트")
    void setAndGetBodyAge() {
        int bodyAge = 28;

        community.setBodyAge(bodyAge);

        assertThat(community.getBodyAge()).isEqualTo(bodyAge);
    }

    @Test
    @DisplayName("전체 필드 설정 테스트")
    void setAllFields() {
        String content = "전체 필드 테스트 게시글";
        String userNm = "fullTestUser";
        Date regDate = new Date();
        String userId = "999";
        int heartrate = 75;
        double temperature = 37.0;
        double bloodpress = 110.0;
        int smoking = 0;
        int drinking = 1;
        int exercise = 30;
        int age = 25;
        int bodyAge = 23;

        community.setContent(content);
        community.setUserNm(userNm);
        community.setRegDate(regDate);
        community.setUserId(userId);
        community.setHeartrate(heartrate);
        community.setTemperature(temperature);
        community.setBloodpress(bloodpress);
        community.setSmoking(smoking);
        community.setDrinking(drinking);
        community.setExercise(exercise);
        community.setAge(age);
        community.setBodyAge(bodyAge);

        assertThat(community.getContent()).isEqualTo(content);
        assertThat(community.getUserNm()).isEqualTo(userNm);
        assertThat(community.getRegDate()).isEqualTo(regDate);
        assertThat(community.getUserId()).isEqualTo(userId);
        assertThat(community.getHeartrate()).isEqualTo(heartrate);
        assertThat(community.getTemperature()).isEqualTo(temperature);
        assertThat(community.getBloodpress()).isEqualTo(bloodpress);
        assertThat(community.getSmoking()).isEqualTo(smoking);
        assertThat(community.getDrinking()).isEqualTo(drinking);
        assertThat(community.getExercise()).isEqualTo(exercise);
        assertThat(community.getAge()).isEqualTo(age);
        assertThat(community.getBodyAge()).isEqualTo(bodyAge);
    }

    @Test
    @DisplayName("null 값 처리 테스트")
    void handleNullValues() {
        community.setContent(null);
        community.setUserNm(null);
        community.setRegDate(null);

        assertThat(community.getContent()).isNull();
        assertThat(community.getUserNm()).isNull();
        assertThat(community.getRegDate()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 처리 테스트")
    void handleEmptyStrings() {
        String emptyContent = "";
        String emptyUserNm = "";

        community.setContent(emptyContent);
        community.setUserNm(emptyUserNm);

        assertThat(community.getContent()).isEqualTo(emptyContent);
        assertThat(community.getUserNm()).isEqualTo(emptyUserNm);
    }
}
