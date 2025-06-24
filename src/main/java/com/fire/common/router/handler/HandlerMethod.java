package com.fire.common.router.handler;

import java.lang.reflect.Method;

/**
 * 简单的数据载体
 */
public class HandlerMethod {
    private final Object controllerBean;
    private final Method method;
    public HandlerMethod(Object controllerBean, Method method) {
        this.controllerBean = controllerBean;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public Object getControllerBean() {
        return controllerBean;
    }
}
