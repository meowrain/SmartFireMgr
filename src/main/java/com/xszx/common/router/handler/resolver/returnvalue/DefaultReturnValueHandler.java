package com.xszx.common.router.handler.resolver.returnvalue;

import com.xszx.common.router.handler.resolver.ReturnValueHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 默认返回值处理器（用于非@ResponseBody方法）
 */
public class DefaultReturnValueHandler implements ReturnValueHandler {

    @Override
    public boolean supports(Method method) {
        // 作为默认处理器，支持所有方法
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, Method method,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 对于非@ResponseBody方法，通常不需要处理返回值
        // 方法内部应该直接写入response
        if (returnValue != null && returnValue instanceof String) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(returnValue.toString());
        }
        // 如果返回值为null或void，则不做任何处理（方法内部已经处理了response）
    }
}
