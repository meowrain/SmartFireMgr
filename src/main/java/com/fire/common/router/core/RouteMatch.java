package com.fire.common.router.core;

import com.fire.common.router.handler.HandlerMethod;

import java.util.Map;

/**
 * 路由匹配类
 */
public class RouteMatch {
    private final HandlerMethod handlerMethod;
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
