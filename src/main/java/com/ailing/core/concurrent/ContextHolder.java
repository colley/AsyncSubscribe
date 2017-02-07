/**
 * Copyright (C), 2011-2016 The Store
 * File Name: ContextEntity.java
 * Encoding: UTF-8
 * Date: Sep 7, 2011
 * History:
 */
package com.ailing.core.concurrent;

import java.util.HashMap;
import java.util.Map;


/**
 * @author colley
 * @version 1.0  Date: 17-2-7 下午4:18
 */
public class ContextHolder implements java.io.Serializable {
    private static final long serialVersionUID = 1315514516550327416L;
    static ThreadLocal<ContextHolder> actionContext = new ContextThreadLocal();
    private Map<String, Object> context;

    public ContextHolder(Map<String, Object> context) {
        this.context = context;
    }

    public Object get(String key) {
        return context.get(key);
    }

    public static void setContext(ContextHolder context) {
        actionContext.set(context);
    }

    public static ContextHolder getContext() {
    	ContextHolder context = actionContext.get();

        if (context == null) {
            context = new ContextHolder(new HashMap<String, Object>());
            setContext(context);
        }

        return context;
    }

    public void setContextMap(Map<String, Object> contextMap) {
        getContext().context = contextMap;
    }

    public Map<String, Object> getContextMap() {
        return context;
    }

    public void put(String key, Object value) {
        context.put(key, value);
    }

    private static class ContextThreadLocal extends ThreadLocal<ContextHolder> {
        protected ContextHolder initialValue() {
            return new ContextHolder(new HashMap<String, Object>());
        }
    }
}
