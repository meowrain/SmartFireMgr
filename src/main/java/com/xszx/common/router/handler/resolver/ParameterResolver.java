package com.xszx.common.router.handler.resolver;

import com.xszx.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

/**
 * 参数解析器接口
 */
public interface ParameterResolver {
    /**
     * 判断该解析器是否支持当前参数
     */
    boolean supports(Parameter parameter);

    /**
     * 解析参数值
     */
    Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch);
}
