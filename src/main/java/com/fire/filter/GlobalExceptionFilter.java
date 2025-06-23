package com.fire.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fire.util.JacksonHolderSingleton;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局拦截器
 */
@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {
    private static final ObjectMapper MAPPER = JacksonHolderSingleton.getObjectMapper();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // ★★★ 核心：将请求传递给责任链的下一个节点 ★★★
            // 如果后续节点（其他Filter或Servlet）抛出异常，将会被下面的catch块捕获。
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception e) {
            // 在服务器控制台打印错误日志，方便调试。
            // 使用日志框架SLF4J + Logback处理错误
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
