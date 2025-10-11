package com.sleekydz86.web.common.pagination;

public class DefaultPaginationRenderer extends AbstractPaginationRenderer {

    public DefaultPaginationRenderer() {
        firstPageLabel = "<a href=\"#\" class=\"ar1\" onclick=\"{0}({1}); return false;\"><img src=\"/images/bbs/al_icon3.png\" alt=\"처음페이지\"></a>";
        previousPageLabel = "<a href=\"#\" class=\"ar\" onclick=\"{0}({1}); return false;\"><img src=\"/images/bbs/al_icon2.png\" alt=\"이전 페이지\"></a>";
        currentPageLabel = "<a href=\"#\" class=\"on\">{0}</a>";
        otherPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">{2}</a>";
        nextPageLabel = "<a href=\"#\" class=\"ar\" onclick=\"{0}({1}); return false;\"><img src=\"/images/bbs/ar_icon2.png\" alt=\"다음페이지\"></a>";
        lastPageLabel = "<a href=\"#\" class=\"ar1\" onclick=\"{0}({1}); return false;\"><img src=\"/images/bbs/ar_icon3.png\" alt=\"마지막페이지\"></a>";
    }

    @Override
    public String renderPagination(PaginationInfo paginationInfo, String jsFunction) {

        return super.renderPagination(paginationInfo, jsFunction);
    }

}