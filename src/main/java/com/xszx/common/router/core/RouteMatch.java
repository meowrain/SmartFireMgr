package com.xszx.common.router.core;

import com.xszx.common.router.handler.HandlerMethod;

import java.util.Map;

/**
 * 路由匹配类
 */
public class RouteMatch {
    // 请求方法
    private final HandlerMethod handlerMethod;
    // 请求的动态路由
    private final Map<String,String> pathVariables;
    public RouteMatch(HandlerMethod handlerMethod, Map<String,String> pathVariables) {
        this.handlerMethod = handlerMethod;
        this.pathVariables = pathVariables;
    }

    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }
}
