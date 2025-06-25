package com.xszx.common.router.handler.resolver.param;

import com.xszx.common.router.handler.resolver.ParameterResolver;
import com.xszx.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

/**
 * 这个类是一个参数解析器，专门负责在Controller方法中自动注入 HttpServletResponse 对象。
 */
public class ResponseParameterResolver implements ParameterResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return HttpServletResponse.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter p, HttpServletRequest req, HttpServletResponse res, RouteMatch match) {
        return res;
    }
}
