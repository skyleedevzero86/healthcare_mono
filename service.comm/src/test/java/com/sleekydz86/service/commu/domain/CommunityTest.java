package com.sleekydz86.service.commu.domain;

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
        // when & then
        assertThat(community).isNotNull();
        assertThat(community.getCommuSeq()).isEqualTo(0);
        assertThat(community.getContent()).isNull();
        assertThat(community.getRegDate()).isNull();
    }

    @Test
    @DisplayName("게시글 내용 설정 및 조회 테스트")
    void setAndGetContent() {
        // given
        String content = "테스트 게시글 내용입니다.";

        // when
        community.setContent(content);

        // then
        assertThat(community.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("사용자명 설정 및 조회 테스트")
    void setAndGetUserNm() {
        // given
        String userNm = "testUser";

        // when
        community.setUserNm(userNm);

        // then
        assertThat(community.getUserNm()).isEqualTo(userNm);
    }

    @Test
    @DisplayName("등록일 설정 및 조회 테스트")
    void setAndGetRegDate() {
        // given
        Date regDate = new Date();

        // when
        community.setRegDate(regDate);

        // then
        assertThat(community.getRegDate()).isEqualTo(regDate);
    }

    @Test
    @DisplayName("사용자 ID 설정 및 조회 테스트")
    void setAndGetUserId() {
        // given
        int userId = 123;

        // when
        community.setUserId(userId);

        // then
        assertThat(community.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("심박수 설정 및 조회 테스트")
    void setAndGetHeartrate() {
        // given
        int heartrate = 80;

        // when
        community.setHeartrate(heartrate);

        // then
        assertThat(community.getHeartrate()).isEqualTo(heartrate);
    }

    @Test
    @DisplayName("체온 설정 및 조회 테스트")
    void setAndGetTemperature() {
        // given
        int temperature = 36;

        // when
        community.setTemperature(temperature);

        // then
        assertThat(community.getTemperature()).isEqualTo(temperature);
    }

    @Test
    @DisplayName("혈압 설정 및 조회 테스트")
    void setAndGetBloodpress() {
        // given
        int bloodpress = 120;

        // when
        community.setBloodpress(bloodpress);

        // then
        assertThat(community.getBloodpress()).isEqualTo(bloodpress);
    }

    @Test
    @DisplayName("흡연 여부 설정 및 조회 테스트")
    void setAndGetSmoking() {
        // given
        int smoking = 1;

        // when
        community.setSmoking(smoking);

        // then
        assertThat(community.getSmoking()).isEqualTo(smoking);
    }

    @Test
    @DisplayName("음주 여부 설정 및 조회 테스트")
    void setAndGetDrinking() {
        // given
        int drinking = 0;

        // when
        community.setDrinking(drinking);

        // then
        assertThat(community.getDrinking()).isEqualTo(drinking);
    }

    @Test
    @DisplayName("운동량 설정 및 조회 테스트")
    void setAndGetExercise() {
        // given
        int exercise = 3;

        // when
        community.setExercise(exercise);

        // then
        assertThat(community.getExercise()).isEqualTo(exercise);
    }

    @Test
    @DisplayName("나이 설정 및 조회 테스트")
    void setAndGetAge() {
        // given
        int age = 30;

        // when
        community.setAge(age);

        // then
        assertThat(community.getAge()).isEqualTo(age);
    }

    @Test
    @DisplayName("체력 나이 설정 및 조회 테스트")
    void setAndGetBodyAge() {
        // given
        int bodyAge = 28;

        // when
        community.setBodyAge(bodyAge);

        // then
        assertThat(community.getBodyAge()).isEqualTo(bodyAge);
    }

    @Test
    @DisplayName("전체 필드 설정 테스트")
    void setAllFields() {
        // given
        String content = "전체 필드 테스트 게시글";
        String userNm = "fullTestUser";
        Date regDate = new Date();
        int userId = 999;
        int heartrate = 75;
        int temperature = 37;
        int bloodpress = 110;
        int smoking = 0;
        int drinking = 1;
        int exercise = 2;
        int age = 25;
        int bodyAge = 23;

        // when
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

        // then
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
        // when
        community.setContent(null);
        community.setUserNm(null);
        community.setRegDate(null);

        // then
        assertThat(community.getContent()).isNull();
        assertThat(community.getUserNm()).isNull();
        assertThat(community.getRegDate()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 처리 테스트")
    void handleEmptyStrings() {
        // given
        String emptyContent = "";
        String emptyUserNm = "";

        // when
        community.setContent(emptyContent);
        community.setUserNm(emptyUserNm);

        // then
        assertThat(community.getContent()).isEqualTo(emptyContent);
        assertThat(community.getUserNm()).isEqualTo(emptyUserNm);
    }

    @Test
    @DisplayName("음수 값 처리 테스트")
    void handleNegativeValues() {
        // given
        int negativeUserId = -1;
        int negativeHeartrate = -10;
        int negativeAge = -5;

        // when
        community.setUserId(negativeUserId);
        community.setHeartrate(negativeHeartrate);
        community.setAge(negativeAge);

        // then
        assertThat(community.getUserId()).isEqualTo(negativeUserId);
        assertThat(community.getHeartrate()).isEqualTo(negativeHeartrate);
        assertThat(community.getAge()).isEqualTo(negativeAge);
    }

    @Test
    @DisplayName("0 값 처리 테스트")
    void handleZeroValues() {
        // given
        int zeroUserId = 0;
        int zeroHeartrate = 0;
        int zeroAge = 0;

        // when
        community.setUserId(zeroUserId);
        community.setHeartrate(zeroHeartrate);
        community.setAge(zeroAge);

        // then
        assertThat(community.getUserId()).isEqualTo(zeroUserId);
        assertThat(community.getHeartrate()).isEqualTo(zeroHeartrate);
        assertThat(community.getAge()).isEqualTo(zeroAge);
    }

    @Test
    @DisplayName("큰 값 처리 테스트")
    void handleLargeValues() {
        // given
        int largeUserId = Integer.MAX_VALUE;
        int largeHeartrate = 200;
        int largeAge = 150;

        // when
        community.setUserId(largeUserId);
        community.setHeartrate(largeHeartrate);
        community.setAge(largeAge);

        // then
        assertThat(community.getUserId()).isEqualTo(largeUserId);
        assertThat(community.getHeartrate()).isEqualTo(largeHeartrate);
        assertThat(community.getAge()).isEqualTo(largeAge);
    }

    @Test
    @DisplayName("Community 객체 동등성 테스트")
    void communityEquality() {
        // given
        Community community1 = new Community();
        Community community2 = new Community();

        community1.setContent("같은 내용");
        community1.setUserNm("같은 사용자");
        community2.setContent("같은 내용");
        community2.setUserNm("같은 사용자");

        // when & then
        assertThat(community1).isNotEqualTo(community2);
        assertThat(community1.getContent()).isEqualTo(community2.getContent());
        assertThat(community1.getUserNm()).isEqualTo(community2.getUserNm());
    }

    @Test
    @DisplayName("Community 객체 해시코드 테스트")
    void communityHashCode() {
        // given
        Community community1 = new Community();
        Community community2 = new Community();

        // when & then
        assertThat(community1.hashCode()).isNotEqualTo(community2.hashCode());
    }
}
