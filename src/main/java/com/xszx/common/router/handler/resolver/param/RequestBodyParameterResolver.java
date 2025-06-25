package com.xszx.common.router.handler.resolver.param;

import com.xszx.common.router.interfaces.RequestBody;
import com.xszx.common.router.handler.resolver.ParameterResolver;
import com.xszx.common.router.core.RouteMatch;
import com.xszx.util.JacksonHolderSingleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.lang.reflect.Parameter;

/**
 * 请求体解析器 把请求体转换为想要的Object
 */
public class RequestBodyParameterResolver implements ParameterResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch) {
        try {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
            }

            String json = jsonBuilder.toString();
            if (json.isEmpty()) {
                return null;
            }
            // 使用jackson把对应的json对象转换为Object
            return JacksonHolderSingleton.getObjectMapper()
                    .readValue(json, parameter.getType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request body", e);
        }
    }
}
