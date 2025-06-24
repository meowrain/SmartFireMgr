package com.fire.common.router.handler.resolver.param;

import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

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
