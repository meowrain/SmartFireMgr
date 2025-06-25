package com.xszx.common.router.interceptor;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xszx.common.exceptions.AbstractException;
import com.xszx.common.exceptions.ClientException;
import com.xszx.common.exceptions.ServiceException;
import com.xszx.common.errorcode.BaseErrorCode;
import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;
import com.xszx.util.JacksonHolderSingleton;

public class GlobalExceptionInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true; // 继续处理请求
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO Auto-generated method stub
        HandlerInterceptor.super.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 处理异常情况
        if (ex != null) {
            handleException(request, response, ex);
        }
    }

    /**
     * 处理自定义异常
     *
     * @param request  {@link HttpServletRequest} HTTP请求对象
     * @param response {@link HttpServletResponse} HTTP响应对象
     * @param ex       {@link Exception} 异常对象
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        try {
            if (response.isCommitted()) {
                log.warn("Response已提交，无法处理异常: {}", ex.getMessage());
                return;
            }

            Exception actualException = unwrapException(ex);
            // 根据异常类型进行分类处理
            if (actualException instanceof AbstractException) {
                handleCustomException(request, response, (AbstractException) actualException);
            } else {
                // 处理未知异常
                handleUnknownException(request, response, actualException);
            }
        } catch (Exception e) {
            log.error("处理异常时发生错误: {}", e.getMessage(), e);
            // TODO: 可以在这里记录日志或进行其他处理
        }
    }

    private void handleCustomException(HttpServletRequest request, HttpServletResponse response, AbstractException ex) {
        try {
            // 根据异常类型确定HTTP状态码和日志级别
            int httpStatus;
            if (ex instanceof ClientException) {
                // 客户端异常通常使用400系列状态码
                httpStatus = determineClientErrorStatus(ex);
                String requestInfo = buildRequestInfo(request);
                log.warn("客户端异常 - {} | ErrorCode: {} | Message: {}", requestInfo, ex.errorCode, ex.errorMessage);
                // 如果有根本异常信息，可以记录下来
                if (ex.getCause() != null) {
                    log.warn("客户端异常根本异常信息: {}", ex.getCause().getMessage());
                }
            } else if (ex instanceof ServiceException) {
                // 服务端异常通常使用500系列状态码
                httpStatus = determineServiceErrorStatus(ex);
                String requestInfo = buildRequestInfo(request);
                log.error("服务端异常 - {} | ErrorCode: {} | Message: {}", requestInfo, ex.errorCode, ex.errorMessage, ex);
                // 如果有根本异常信息，可以记录下来
                if (ex.getCause() != null) {
                    log.error("服务端异常根本异常信息: {}", ex.getCause().getMessage(), ex.getCause());
                }
            } else {
                httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                String requestInfo = buildRequestInfo(request);
                log.error("未知自定义异常 - {} | ErrorCode: {} | Message: {}", requestInfo, ex.errorCode, ex.errorMessage, ex);
            }

            // 写入自定义异常响应
            writeCustomExceptionResponse(response, ex, httpStatus);

        } catch (Exception e) {
            log.error("处理自定义异常时发生错误: {}", e.getMessage(), e);
            writeSimpleErrorResponse(response);
        }
    }

    private void handleUnknownException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        try {
            String requestInfo = buildRequestInfo(request);
            log.error("未知异常 - {} | Exception: {}", requestInfo, ex.getClass().getSimpleName(), ex);

            // 写入未知异常响应
            writeUnknownExceptionResponse(response, ex);

        } catch (Exception e) {
            log.error("处理未知异常时发生错误: {}", e.getMessage(), e);
            writeSimpleErrorResponse(response);
        }
    }

    /**
     * 写入自定义异常响应
     */
    private void writeCustomExceptionResponse(HttpServletResponse response, AbstractException ex, int httpStatus) {
        try {
            response.reset();
            response.setStatus(httpStatus);
            response.setContentType("application/json;charset=UTF-8");

            // 使用异常中的错误码和错误信息
            Result<Void> result = ResultBuilder.failure(ex.errorCode, ex.errorMessage);
            String json = JacksonHolderSingleton.getObjectMapper().writeValueAsString(result);

            response.getWriter().write(json);
            response.getWriter().flush();

        } catch (Exception e) {
            log.error("写入自定义异常响应失败", e);
            writeSimpleErrorResponse(response);
        }
    }

    /**
     * 写入未知异常响应
     */
    private void writeUnknownExceptionResponse(HttpServletResponse response, Exception ex) {
        try {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");

            // 对于未知异常，不暴露具体异常信息
            Result<Void> result = ResultBuilder.failure(
                    BaseErrorCode.SERVICE_ERROR.code(),
                    "系统异常，请稍后重试");
            String json = JacksonHolderSingleton.getObjectMapper().writeValueAsString(result);

            response.getWriter().write(json);
            response.getWriter().flush();

        } catch (Exception e) {
            log.error("写入未知异常响应失败", e);
            writeSimpleErrorResponse(response);
        }
    }

    /**
     * 写入简单的错误响应（当JSON序列化失败时的最后兜底）
     */
    private void writeSimpleErrorResponse(HttpServletResponse response) {
        try {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"服务器内部错误\"}");
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("写入简单错误响应也失败了，请检查系统状态", e);
        }
    }

    /**
     * 根据ClientException确定具体的HTTP状态码
     */
    private int determineClientErrorStatus(AbstractException ex) {
        // 可以根据具体的错误码来确定更精确的状态码
        String errorCode = ex.errorCode;

        // 这里可以根据您的错误码规范进行映射
        if (errorCode.contains("PARAM")) {
            return HttpServletResponse.SC_BAD_REQUEST; // 400
        } else if (errorCode.contains("AUTH")) {
            return HttpServletResponse.SC_UNAUTHORIZED; // 401
        } else if (errorCode.contains("FORBIDDEN")) {
            return HttpServletResponse.SC_FORBIDDEN; // 403
        } else if (errorCode.contains("NOT_FOUND")) {
            return HttpServletResponse.SC_NOT_FOUND; // 404
        } else {
            return HttpServletResponse.SC_BAD_REQUEST; // 默认400
        }
    }

    private int determineServiceErrorStatus(AbstractException ex) {
        // 可以根据具体的错误码来确定更精确的状态码
        String errorCode = ex.errorCode;

        // 这里可以根据您的错误码规范进行映射
        if (errorCode.contains("SERVICE_UNAVAILABLE")) {
            return HttpServletResponse.SC_SERVICE_UNAVAILABLE; // 503
        } else if (errorCode.contains("INTERNAL_SERVER_ERROR")) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
        } else {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 默认500
        }
    }

    private String buildRequestInfo(HttpServletRequest request) {
        return String.format("Method: %s, URI: %s, RemoteAddr: %s",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());
    }

    /**
     * 解包装异常，获取真正的异常对象
     * 
     * @param ex 可能被包装的异常
     * @return 实际的异常对象
     */
    private Exception unwrapException(Exception ex) {
        // 如果是反射调用异常，获取其根本原因
        if (ex instanceof InvocationTargetException) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                return (Exception) cause;
            }
        }

        // 其他情况直接返回原异常
        return ex;
    }
}
