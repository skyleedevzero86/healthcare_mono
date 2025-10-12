package com.sleekydz86.service.auth.dto;

import com.google.common.base.CaseFormat;

import java.util.HashMap;

public class CamelHashMap extends HashMap<Object, Object> {

    private static final long serialVersionUID = 1L;

    public Object put(Object key, Object value) {
        return super.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, (String) key), value);
    }
}
