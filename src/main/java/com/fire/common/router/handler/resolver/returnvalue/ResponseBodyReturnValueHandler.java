package com.fire.common.router.handler.resolver.returnvalue;

import com.fire.common.router.interfaces.ResponseBody;
import com.fire.common.router.handler.resolver.ReturnValueHandler;
import com.fire.util.JacksonHolderSingleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ResponseBody注解的返回值处理器
 */
public class ResponseBodyReturnValueHandler implements ReturnValueHandler {

    @Override
    public boolean supports(Method method) {
        return method.isAnnotationPresent(ResponseBody.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, Method method,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (returnValue == null) {
            response.getWriter().write("null");
            return;
        }

        // 将返回值序列化为JSON
        String json = JacksonHolderSingleton.getObjectMapper().writeValueAsString(returnValue);
        response.getWriter().write(json);
    }
}
