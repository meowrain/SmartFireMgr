package com.xszx.common.filter;

import com.xszx.common.exceptions.ServiceException;
import com.xszx.util.JwtUtil;
import com.xszx.util.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器
 * 拦截所有请求，进行JWT认证检查
 */
public class AuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    // 除了白名单外，还需要放行的静态资源和登录相关页面
    private static final List<String> STATIC_RESOURCES = Arrays.asList(
            "/login.jsp",
            "/register.jsp",
            "/css/",
            "/js/",
            "/images/",
            "/fonts/",
            "/vendors/",
            "/favicon.ico",
            ".css",
            ".js",
            ".png",
            ".jpg",
            ".jpeg",
            ".gif",
            ".ico",
            ".woff",
            ".woff2",
            ".ttf",
            ".eot");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取请求路径
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        log.debug("AuthFilter processing request: {}", requestPath);

        // 检查是否为静态资源
        if (isStaticResource(requestPath)) {
            log.debug("静态资源请求，放行: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        // 检查白名单
        if (WhiteList.isInWhiteList(requestPath)) {
            log.debug("白名单请求，放行: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        // 进行JWT认证
        try {
            String rawToken = httpRequest.getHeader("Authorization");

            if (rawToken == null || !rawToken.startsWith("Bearer ")) {
                log.warn("请求路径 [{}] 缺少有效的Authorization头", requestPath);
                handleUnauthorized(httpRequest, httpResponse, "需要登录才能访问");
                return;
            }

            // 提取并验证token
            String token = rawToken.substring(7);
            JwtUtil.validateToken(token);

            log.debug("JWT认证成功: {}", requestPath);
            chain.doFilter(request, response);

        } catch (ServiceException e) {
            log.warn("JWT认证失败 [{}]: {}", requestPath, e.getMessage());
            handleUnauthorized(httpRequest, httpResponse, e.getMessage());
        } catch (Exception e) {
            log.error("认证过程中发生异常 [{}]: {}", requestPath, e.getMessage());
            handleUnauthorized(httpRequest, httpResponse, "认证失败");
        }
    }

    /**
     * 检查是否为静态资源
     */
    private boolean isStaticResource(String requestPath) {
        if (requestPath == null) {
            return false;
        }

        return STATIC_RESOURCES.stream().anyMatch(resource -> {
            if (resource.endsWith("/")) {
                return requestPath.startsWith(resource);
            } else if (resource.startsWith(".")) {
                return requestPath.endsWith(resource);
            } else {
                return requestPath.equals(resource);
            }
        });
    }

    /**
     * 处理未授权访问
     */
    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {

        String requestPath = request.getRequestURI().substring(request.getContextPath().length());

        // 如果是API请求，返回JSON错误响应
        if (requestPath.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String jsonResponse = String.format(
                    "{\"success\":false,\"errorCode\":\"A00001\",\"errorMessage\":\"%s\"}",
                    message);
            response.getWriter().write(jsonResponse);
        } else {
            // 如果是页面请求，重定向到登录页
            log.info("未授权访问页面 [{}]，重定向到登录页", requestPath);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void destroy() {
        log.info("AuthFilter destroyed");
    }
}
