package com.xszx.common.router.handler.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 返回值处理器接口
 */
public interface ReturnValueHandler {

    /**
     * 判断该处理器是否支持当前方法的返回值
     */
    boolean supports(Method method);

    /**
     * 处理返回值
     */
    void handleReturnValue(Object returnValue, Method method,
            HttpServletRequest request, HttpServletResponse response) throws Exception;
}
