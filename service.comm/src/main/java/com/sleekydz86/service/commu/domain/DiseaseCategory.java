package com.sleekydz86.service.commu.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DiseaseCategory {
    //enum값으로 하지않고 게시글을 통해 언제든 추가될 수 있도록 내장객체로 함.

    private Long categoryId;
    private String diseaseCate;
    //CANCER,LEUKEMIA,HYPERTENSION,DIABETES,ANGINA,MYOCARDIAL,INFARCTION,MITRAL,VALVE,PROLAPSE,STROKE,LIVERCIRRHOSIS,AIDS
    //1. 암 2. 백혈병 3. 고혈압 4. 당뇨병 5. 협심증 6. 심근경색증 7. 심장판막증 8. 뇌졸중(뇌경색, 뇌출혈) 9. 간질환(간경화) 10. 에이즈
}