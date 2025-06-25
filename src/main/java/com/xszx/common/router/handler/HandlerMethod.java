package com.xszx.common.router.handler;

import java.lang.reflect.Method;

/**
 * HandlerMethod 类用于封装控制器方法的信息，
 * 包括控制器实例和方法本身。
 * 这个类通常在路由匹配过程中使用，用于存储匹配到的处理方法。
 */
public class HandlerMethod {
    private final Object controllerBean;
    private final Method method;

    public HandlerMethod(Object controllerBean, Method method) {
        this.controllerBean = controllerBean;
        this.method = method;
    }

    /**
     * 获取控制器方法
     * 
     * @return
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取控制器实例
     * 
     * @return
     */
    public Object getControllerBean() {
        return controllerBean;
    }
}
