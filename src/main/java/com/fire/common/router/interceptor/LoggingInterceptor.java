package com.fire.common.router.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 日志拦截器 - 记录请求处理的详细日志信息
 * 
 * 功能：
 * 1. 记录请求开始和结束时间
 * 2. 计算请求处理耗时
 * 3. 记录请求参数和响应状态
 * 4. 为每个请求生成唯一的traceId便于链路追踪
 * 5. 使用MDC在整个请求生命周期内传递上下文信息
 */
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    // 请求开始时间的属性名
    private static final String START_TIME_ATTR = "REQUEST_START_TIME";
    // TraceId的属性名
    private static final String TRACE_ID_ATTR = "TRACE_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        // 生成唯一的TraceId（可以从请求头获取，或者自己生成）
        String traceId = generateTraceId(request);
        request.setAttribute(TRACE_ID_ATTR, traceId);

        // 将TraceId放入MDC，在整个请求处理过程中都能使用
        MDC.put("traceId", traceId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        // 记录请求开始日志
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("🚀 请求开始 - {} {} {} | Client: {} | UserAgent: {}",
                method,
                uri + (queryString != null ? "?" + queryString : ""),
                traceId,
                remoteAddr,
                userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) + "..." : "Unknown");

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录响应信息
        int status = response.getStatus();
        String contentType = response.getContentType();

        log.info("📤 响应处理完成 - Status: {} | ContentType: {}", status, contentType);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        try {
            // 计算请求处理时间
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            long duration = startTime != null ? System.currentTimeMillis() - startTime : -1;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            if (ex != null) {
                // 有异常的情况
                log.warn("💥 请求异常结束 - {} {} | Status: {} | Duration: {}ms | Exception: {}",
                        method, uri, status, duration, ex.getClass().getSimpleName());
            } else {
                // 正常结束
                String level = determineLogLevel(duration, status);
                if ("WARN".equals(level)) {
                    log.warn("⚡ 请求缓慢结束 - {} {} | Status: {} | Duration: {}ms",
                            method, uri, status, duration);
                } else {
                    log.info("✅ 请求正常结束 - {} {} | Status: {} | Duration: {}ms",
                            method, uri, status, duration);
                }
            }

        } finally {
            // 清理MDC，避免内存泄漏
            MDC.clear();
        }
    }

    /**
     * 生成TraceId
     */
    private String generateTraceId(HttpServletRequest request) {
        // 首先尝试从请求头获取（如果有前端或网关传递）
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId != null && !traceId.trim().isEmpty()) {
            return traceId.trim();
        }

        // 如果没有，生成一个简单的TraceId
        return String.format("%d-%04d",
                System.currentTimeMillis(),
                (int) (Math.random() * 10000));
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // 尝试从各种代理头获取真实IP
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For可能包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 如果都没有，返回远程地址
        return request.getRemoteAddr();
    }

    /**
     * 根据响应时间和状态码确定日志级别
     */
    private String determineLogLevel(long duration, int status) {
        // 响应时间超过1秒，或者状态码异常，使用WARN级别
        if (duration > 1000 || status >= 400) {
            return "WARN";
        }
        return "INFO";
    }
}
