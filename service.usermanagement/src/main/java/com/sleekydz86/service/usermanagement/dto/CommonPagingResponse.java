package com.sleekydz86.service.usermanagement.dto;

import com.sleekydz86.service.usermanagement.global.util.PaginationInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommonPagingResponse {

    private List<?> list;

    private int totalCount;
    private PaginationInfo paginationInfo;
}