package com.sleekydz86.service.healthcare.dto;

import org.springframework.stereotype.Component;

@Component
public class AIHandleDto {
    String aiRequestQuery;


    public String getQuery(BioInfoDto bioInfoDto) {
        this.aiRequestQuery = "역할: 사용자들의 바이오 정보로 상태를 파악하고 필요한 진단을 내리는 종합병원전문의.\n"+
                "심박수 " + bioInfoDto.getHeartrate() +", 최대혈압 " +  bioInfoDto.getBloodpressMax() + ", 최소혈압 " + bioInfoDto.getBloodpressMin() +
                ", 체온 " + bioInfoDto.getTemperature() + ", 스트레스 " + bioInfoDto.getStress() +
                "(50이 최대 불건강) 인 "+bioInfoDto.getAge()+"세 "+
                "사람이 주의해야 할 질병 3가지와 해당 질병에 좋은 음식을 간단하게 추천해준후 3줄정도의 코멘트를 추가\n" +
                bioInfoDto.getUserNm() + "님이 주의하여야 하는 질병 및 음식 추천\n" +
                "간결하고 정중한 어체, 맨 첫글자는 사용자 이름으로 시작\n" +
                "주의 질병3가지 모두 빨간색처리하도록 <span style=\"color:#F55F5F;\"></span>안에 쓸것\n" +
                "추천하는 음식키워드는 모두 파란색처리하도록 <span style=\"color: #325CF0;\"></span>안에 쓸것\n" +
                "코드가 아닌 text로 표현해라.";

        return this.aiRequestQuery;
    }

}
