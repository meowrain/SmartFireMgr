package com.fire.common.router.handler.resolver.param;

import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

/**
 * 请求参数解析器,负责支持原生servlet,专门负责在Controller方法中自动注入 HttpServletRequest 对象。
 */
public class RequestParameterResolver implements ParameterResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return HttpServletRequest.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter p, HttpServletRequest req, HttpServletResponse res, RouteMatch match) {
        return req;
    }
}
