package com.sleekydz86.web.common;

import com.sleekydz86.web.common.pagination.DefaultPaginationManager;
import com.sleekydz86.web.common.pagination.PaginationInfo;
import com.sleekydz86.web.common.pagination.PaginationManager;
import com.sleekydz86.web.common.pagination.PaginationRenderer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.springframework.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;

public class PaginationTag extends TagSupport {

    private static final long serialVersionUID = 6069751239159427455L;
    private PaginationInfo paginationInfo;
    private String type;
    private String jsFunction;

    public int doEndTag() throws JspException {

        try {

            JspWriter out = pageContext.getOut();
            PageContext context = (PageContext) pageContext;
            HttpServletRequest request = (HttpServletRequest) context.getRequest();

            PaginationManager paginationManager;

            WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(request,
                    pageContext.getServletContext());

            if (ctx.containsBean("paginationManager")) {
                paginationManager = (PaginationManager) ctx.getBean("paginationManager");
            } else {
                paginationManager = new DefaultPaginationManager();
            }

            PaginationRenderer paginationRenderer = paginationManager.getRendererType(type);

            String contents = paginationRenderer.renderPagination(paginationInfo, jsFunction);

            out.println(contents);

            return EVAL_PAGE;

        } catch (IOException e) {
            throw new JspException();
        }
    }

    public void setJsFunction(String jsFunction) {
        this.jsFunction = jsFunction;
    }

    public void setPaginationInfo(PaginationInfo paginationInfo) {
        this.paginationInfo = paginationInfo;
    }

    public void setType(String type) {
        this.type = type;
    }

}