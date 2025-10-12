package com.sleekydz86.service.usermanagement.dto;

import com.google.common.base.CaseFormat;

import java.util.HashMap;

public class CamelHashMap<K,V> extends HashMap<Object, Object> {

    private static final long serialVersionUID = 1L;


    public Object put(Object key, Object value) {
        return super.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, (String) key), value);
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }

}
