package com.sleekydz86.api.gateway.dto;


import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;

/**
 * Filter의 JSON Return을 위한 ApiResponse toJsonString 클래스
 *
 * */
@Data @Builder
public class ApiResponse {

    private String resultCode;
    private String resultMessage;
    private Object resultData;

    public static String error(ApiResultCode errorCode) {
        return response(errorCode.code, errorCode.message, null);
    }

    private static String response(String code, String message, Object data) {
        ApiResponse apiResponse = ApiResponse.builder()
                .resultCode(code)
                .resultMessage(message)
                .resultData(data)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(apiResponse);
        }catch (Exception e) {
            e.printStackTrace();
            // 수동 에러처리
            return "{\r\n" +
                    "    \"resultCode\": \"" + ApiResultCode.UNKOWN_ERR.code + "\",\r\n" +
                    "    \"resultMessage\": \"" + ApiResultCode.UNKOWN_ERR.message + "\",\r\n" +
                    "    \"resultData\": null\r\n" +
                    "}";
        }


    }
}
