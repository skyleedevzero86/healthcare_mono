package com.sleekydz86.web.common.pagination;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DefaultPaginationManager implements PaginationManager {

    @SuppressWarnings("unused")
    private Map<String, PaginationRenderer> rendererType;

    public void setRendererType(Map<String, PaginationRenderer> rendererType) {
        this.rendererType = rendererType;
    }

    public PaginationRenderer getRendererType(String type) {

        PaginationRenderer renderer = null;

        type = StringUtils.isEmpty(type) ? "" : type;

        switch (type) {
            case "custom":
                renderer = new CustomPaginationRenderer();
                break;
            default:
                renderer = new DefaultPaginationRenderer();
                break;
        }

        return renderer;
    }

}
