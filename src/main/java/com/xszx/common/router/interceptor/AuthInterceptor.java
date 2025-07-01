package com.xszx.common.router.interceptor;

import com.xszx.common.exceptions.ServiceException;
import com.xszx.util.JwtUtil;
import com.xszx.util.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.xszx.common.errorcode.BaseErrorCode.AUTH_ERROR;

public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info(request.toString());
        // 获取请求路径
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());

        if (WhiteList.isInWhiteList(requestPath)) {
            log.info("Request path [{}] 在白名单中，放行.", requestPath);
            // 如果请求路径在白名单中，直接放行
            return true;
        } else {
            log.info("Request path [{}] 不在白名单中，进行权限验证.", requestPath);
        }

        String rawToken = request.getHeader("Authorization");
        log.info("rawToken [{}]", rawToken);
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            log.error("Request path [{}]  请求头中缺少 Authorization 或格式不正确: {}", requestPath, rawToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ServiceException(AUTH_ERROR);
        }

        // 提取实际的token（去掉"Bearer "前缀）
        String token = rawToken.substring(7);
        try {
            JwtUtil.validateToken(token);
            log.info("Token验证成功: {}", requestPath);
        } catch (Exception e) {
            log.error("Token验证失败 [{}]: {}", requestPath, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ServiceException(AUTH_ERROR);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
