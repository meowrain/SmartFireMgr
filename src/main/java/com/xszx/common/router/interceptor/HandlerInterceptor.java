package com.xszx.common.router.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器接口
 */
public interface HandlerInterceptor {
    
    /**
     * 请求处理前执行
     * @return true 继续处理，false 中断处理
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
    
    /**
     * 请求处理后执行
     */
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    }
    
    /**
     * 完成处理后执行（包括异常情况）
     */
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
