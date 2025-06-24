package com.fire.common.router.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器示例
 */
public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        
        System.out.println("=== 请求开始 ===");
        System.out.println("Method: " + method);
        System.out.println("URI: " + uri + (queryString != null ? "?" + queryString : ""));
        System.out.println("Handler: " + handler);
        System.out.println("时间: " + new java.util.Date());
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Response Status: " + response.getStatus());
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            System.out.println("Exception: " + ex.getMessage());
        }
        System.out.println("=== 请求结束 ===\n");
    }
}
