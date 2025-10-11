package com.sleekydz86.web.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchVO implements Serializable {

    private static final long serialVersionUID = 6269112247860707928L;

    private String searchCon1;
    private String searchCon2;
    private String searchCon3;
    private String searchCon4;
    private String searchCon5;
    private String searchCon6;

    private String searchKeyword;

    private String searchType;

    private String searchUseYn;

    private int pageIndex = 1;

    private int pageUnit = 10;

    private int pageSize = 10;

    private int pageOffset = 1;

    private int totalPageSize;

    private int firstIndex = 1;

    private int lastIndex = 1;

    private int recordCountPerPage = 10;

    private String searchDate;
    private String searchYear;
    private String searchMonth;

    protected String order_type;
    protected String order;

    private int limit;
    private int offset;

    private String queryStr0;
    private String queryStr1;
    private String queryStr2;
    private String queryStr3;
    private String queryStr4;
    private String queryStr5;
    private String queryStr6;
    private String queryStr7;
    private String queryStr8;
    private String queryStr9;

    private String servletPath;

    private String scrollTop;

    private String searchCondition;
    private String startRange;
    private String endRange;
    private String isActive;
    private String srchGroupString;
    private List<String> srchGroupList;

    public int getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getRecordCountPerPage() {
        return recordCountPerPage;
    }

    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }

    public String getSearchCon1() {
        return searchCon1;
    }

    public void setSearchCon1(String searchCon1) {
        this.searchCon1 = searchCon1;
    }

    public String getSearchCon2() {
        return searchCon2;
    }

    public void setSearchCon2(String searchCon2) {
        this.searchCon2 = searchCon2;
    }

    public String getSearchCon3() {
        return searchCon3;
    }

    public void setSearchCon3(String searchCon3) {
        this.searchCon3 = searchCon3;
    }

    public String getSearchCon4() {
        return searchCon4;
    }

    public void setSearchCon4(String searchCon4) {
        this.searchCon4 = searchCon4;
    }

    public String getSearchCon5() {
        return searchCon5;
    }

    public void setSearchCon5(String searchCon5) {
        this.searchCon5 = searchCon5;
    }

    public String getSearchCon6() {
        return searchCon6;
    }

    public void setSearchCon6(String searchCon6) {
        this.searchCon6 = searchCon6;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSearchUseYn() {
        return searchUseYn;
    }

    public void setSearchUseYn(String searchUseYn) {
        this.searchUseYn = searchUseYn;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageUnit() {
        return pageUnit;
    }

    public void setPageUnit(int pageUnit) {
        this.pageUnit = pageUnit;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public void setTotalPageSize(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public String getSearchYear() {
        return searchYear;
    }

    public void setSearchYear(String searchYear) {
        this.searchYear = searchYear;
    }

    public String getSearchMonth() {
        return searchMonth;
    }

    public void setSearchMonth(String searchMonth) {
        this.searchMonth = searchMonth;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        offset = (this.getPageIndex() - 1) * this.getPageSize();
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(String scrollTop) {
        this.scrollTop = scrollTop;
    }

    public String getQueryStr0() {
        return queryStr0;
    }

    public void setQueryStr0(String queryStr0) {
        this.queryStr0 = queryStr0;
    }

    public String getQueryStr1() {
        return queryStr1;
    }

    public void setQueryStr1(String queryStr1) {
        this.queryStr1 = queryStr1;
    }

    public String getQueryStr2() {
        return queryStr2;
    }

    public void setQueryStr2(String queryStr2) {
        this.queryStr2 = queryStr2;
    }

    public String getQueryStr3() {
        return queryStr3;
    }

    public void setQueryStr3(String queryStr3) {
        this.queryStr3 = queryStr3;
    }

    public String getQueryStr4() {
        return queryStr4;
    }

    public void setQueryStr4(String queryStr4) {
        this.queryStr4 = queryStr4;
    }

    public String getQueryStr5() {
        return queryStr5;
    }

    public void setQueryStr5(String queryStr5) {
        this.queryStr5 = queryStr5;
    }

    public String getQueryStr6() {
        return queryStr6;
    }

    public void setQueryStr6(String queryStr6) {
        this.queryStr6 = queryStr6;
    }

    public String getQueryStr7() {
        return queryStr7;
    }

    public void setQueryStr7(String queryStr7) {
        this.queryStr7 = queryStr7;
    }

    public String getQueryStr8() {
        return queryStr8;
    }

    public void setQueryStr8(String queryStr8) {
        this.queryStr8 = queryStr8;
    }

    public String getQueryStr9() {
        return queryStr9;
    }

    public void setQueryStr9(String queryStr9) {
        this.queryStr9 = queryStr9;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getStartRange() {
        return startRange;
    }

    public void setStartRange(String startRange) {
        this.startRange = startRange;
    }

    public String getEndRange() {
        return endRange;
    }

    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getSrchGroupString() {
        return srchGroupString;
    }

    public void setSrchGroupString(String srchGroupString) {
        this.srchGroupString = srchGroupString;
    }

    public List<String> getSrchGroupList() {
        return srchGroupList;
    }

    public void setSrchGroupList(List<String> srchGroupList) {
        this.srchGroupList = srchGroupList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}