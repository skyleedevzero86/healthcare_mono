package com.sleekydz86.service.commu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityRequestDto {

    @Min(value = 1, message = "게시글 번호는 1 이상이어야 합니다")
    private Integer commuSeq;

    @NotEmpty(message = "내용은 필수입니다")
    @Size(min = 1, max = 5000, message = "내용은 1자 이상 5000자 이하여야 합니다")
    private String content;

    @Size(max = 50, message = "사용자 ID는 50자 이하여야 합니다")
    @NotEmpty(message = "사용자 ID는 필수입니다")
    private String userId;

    @Size(max = 100, message = "사용자 이름은 100자 이하여야 합니다")
    private String userNm;
}

