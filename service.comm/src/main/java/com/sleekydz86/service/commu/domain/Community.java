package com.sleekydz86.service.commu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Date;

@Entity
@Getter
@Table(name = "health_community")
public class Community {

    @Id
    @GeneratedValue
    @Column(name = "commuId")  //기본키에 매핑, @Access (AccessType.FIELD) 생략됨
    private Long commuId;

    private String userNm;

    @Column(length = 100000000)
    @Lob // 대용량 데이터
    private String content;

    @Temporal(TemporalType.TIMESTAMP) //날짜 시간
    private Date regDate;

    // 내장타입
    @Enumerated(EnumType.STRING) //enum 을 String으로 저장
    private DiseaseCategory category;

    @ManyToOne(fetch = FetchType.EAGER) // 기본전략
    @JoinColumn(name = "userId") // 실제디비엔 user_id 칼럼명으로 들어감
    private User user;
    // DB는 오브젝트를 저장 할 수 없다.FK는 자바는 오브젝트를 저장할 수 없다.
}