package com.sleekydz86.service.healthcare.global.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component("pagingUtil")
public class PagingUtil {

    private Field field = null;

    public PaginationInfo getPageInfo(Object vo, int totalCnt) {

        PaginationInfo paginationInfo = getSetting(vo);
        paginationInfo.setPageSize(10);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    public PaginationInfo getPageInfo(Object vo, int pagingCnt, int totalCnt) {

        PaginationInfo paginationInfo = getSetting(vo);
        pagingCnt = 5;
        paginationInfo.setPageSize(pagingCnt);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    private PaginationInfo getSetting(Object vo) {

        PaginationInfo paginationInfo = new PaginationInfo();

        try {

            field = vo.getClass().getSuperclass().getDeclaredField("pageUnit");
            field.setAccessible(true);
            field.setInt(vo, (int) field.get(vo));

            field = vo.getClass().getSuperclass().getDeclaredField("pageSize");
            field.setAccessible(true);
            int pageSizes = (int) field.get(vo);
            field.setInt(vo, pageSizes);

            field = vo.getClass().getSuperclass().getDeclaredField("pageIndex");
            field.setAccessible(true);
            paginationInfo.setCurrentPageNo((int) field.get(vo));
            paginationInfo.setRecordCountPerPage(pageSizes);

            field = vo.getClass().getSuperclass().getDeclaredField("firstIndex");
            field.setAccessible(true);
            field.setInt(vo, paginationInfo.getFirstRecordIndex());

            field = vo.getClass().getSuperclass().getDeclaredField("lastIndex");
            field.setAccessible(true);
            field.setInt(vo, paginationInfo.getLastRecordIndex());

            field = vo.getClass().getSuperclass().getDeclaredField("recordCountPerPage");
            field.setAccessible(true);
            field.setInt(vo, paginationInfo.getRecordCountPerPage());

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return paginationInfo;
    }

}
