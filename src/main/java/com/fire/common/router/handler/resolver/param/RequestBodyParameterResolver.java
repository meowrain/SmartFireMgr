package com.fire.common.router.handler.resolver.param;

import com.fire.common.router.interfaces.RequestBody;
import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.core.RouteMatch;
import com.fire.util.JacksonHolderSingleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.lang.reflect.Parameter;

/**
 * 请求体参数解析器
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
            
            return JacksonHolderSingleton.getObjectMapper()
                    .readValue(json, parameter.getType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request body", e);
        }
    }
}
