package com.fire.common.router.handler.resolver.param;

import com.fire.common.router.interfaces.PathVariable;
import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.core.RouteMatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

public class PathVariableParameterResolver implements ParameterResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch) {
        PathVariable annotation = parameter.getAnnotation(PathVariable.class);
        String varName = annotation.value();
        String varValue = routeMatch.getPathVariables().get(varName);

        // 在这里可以添加更复杂的类型转换逻辑
        return convert(varValue, parameter.getType());
    }

    // 简单的类型转换器
    private Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        // ... 添加其他类型
        throw new IllegalArgumentException("Unsupported path variable type: " + targetType.getName());
    }
}
