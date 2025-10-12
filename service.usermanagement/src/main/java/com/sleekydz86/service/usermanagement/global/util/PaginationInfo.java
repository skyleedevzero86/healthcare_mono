package com.sleekydz86.service.usermanagement.global.util;

public class PaginationInfo {

    private int currentPageNo;
    private int recordCountPerPage;
    private int pageSize;
    private int totalRecordCount;

    public int getRecordCountPerPage() {
        return recordCountPerPage;
    }

    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    private int totalPageCount;
    private int firstPageNoOnPageList;
    private int lastPageNoOnPageList;
    private int firstRecordIndex;
    private int lastRecordIndex;

    public int getTotalPageCount() {
        totalPageCount = ((getTotalRecordCount() - 1) / getRecordCountPerPage()) + 1;
        return totalPageCount;
    }

    public int getFirstPageNo() {
        return 1;
    }

    public int getLastPageNo() {
        return getTotalPageCount();
    }

    public int getFirstPageNoOnPageList() {
        firstPageNoOnPageList = ((getCurrentPageNo() - 1) / getPageSize()) * getPageSize() + 1;
        return firstPageNoOnPageList;
    }

    public int getLastPageNoOnPageList() {
        lastPageNoOnPageList = getFirstPageNoOnPageList() + getPageSize() - 1;
        if (lastPageNoOnPageList > getTotalPageCount()) {
            lastPageNoOnPageList = getTotalPageCount();
        }
        return lastPageNoOnPageList;
    }

    public int getFirstRecordIndex() {
        firstRecordIndex = (getCurrentPageNo() - 1) * getRecordCountPerPage();
        return firstRecordIndex;
    }

    public int getLastRecordIndex() {
        lastRecordIndex = getCurrentPageNo() * getRecordCountPerPage();
        return lastRecordIndex;
    }

    @Override
    public String toString() {
        return "PaginationInfo [currentPageNo=" + currentPageNo + ", recordCountPerPage=" + recordCountPerPage
                + ", pageSize=" + pageSize + ", totalRecordCount=" + totalRecordCount + ", totalPageCount="
                + totalPageCount + ", firstPageNoOnPageList=" + firstPageNoOnPageList + ", lastPageNoOnPageList="
                + lastPageNoOnPageList + ", firstRecordIndex=" + firstRecordIndex + ", lastRecordIndex="
                + lastRecordIndex + "]";
    }

}