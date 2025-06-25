package com.xszx.common.router.handler.resolver.param;

import com.xszx.common.router.interfaces.RequestParam;
import com.xszx.common.router.handler.resolver.ParameterResolver;
import com.xszx.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

/**
 * 查询参数解析器
 */
public class RequestParamParameterResolver implements ParameterResolver {
    
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch) {
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        String paramName = annotation.value();
        String paramValue = request.getParameter(paramName);
        if (paramValue == null) {
            if (annotation.required()) {
                throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing");
            }
            String defaultValue = annotation.defaultValue();
            paramValue = defaultValue.isEmpty() ? null : defaultValue;
        }
        
        return convert(paramValue, parameter.getType());
    }
    
    private Object convert(String value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == String.class) return value;
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
        if (targetType == Double.class || targetType == double.class) return Double.parseDouble(value);
        
        throw new IllegalArgumentException("Unsupported parameter type: " + targetType.getName());
    }
}
