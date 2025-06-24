package com.fire.common.router.handler;

import com.fire.common.router.core.RouteMatch;
import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.handler.resolver.param.PathVariableParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestParameterResolver;
import com.fire.common.router.handler.resolver.param.ResponseParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestBodyParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestParamParameterResolver;
import com.fire.common.router.interceptor.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HandlerInvoker {
    private final List<ParameterResolver> resolvers = new ArrayList<>();
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    public HandlerInvoker() {
        // 注册所有参数解析器
        resolvers.add(new RequestParameterResolver());
        resolvers.add(new ResponseParameterResolver());
        resolvers.add(new PathVariableParameterResolver());
        resolvers.add(new RequestBodyParameterResolver());
        resolvers.add(new RequestParamParameterResolver());
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch)
            throws Exception {
        HandlerMethod handlerMethod = routeMatch.getHandlerMethod();
        Exception exception = null;

        try {
            // 执行前置拦截器
            for (HandlerInterceptor interceptor : interceptors) {
                if (!interceptor.preHandle(request, response, handlerMethod)) {
                    return; // 中断处理
                }
            }

            // 执行实际的处理方法
            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                args[i] = findResolver(parameter)
                        .resolve(parameter, request, response, routeMatch);
            }

            handlerMethod.getMethod().invoke(handlerMethod.getControllerBean(), args);

            // 执行后置拦截器
            for (HandlerInterceptor interceptor : interceptors) {
                interceptor.postHandle(request, response, handlerMethod);
            }

        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 执行完成拦截器
            for (HandlerInterceptor interceptor : interceptors) {
                try {
                    interceptor.afterCompletion(request, response, handlerMethod, exception);
                } catch (Exception ex) {
                    // 记录日志但不抛出异常
                    ex.printStackTrace();
                }
            }
        }
    }

    private ParameterResolver findResolver(Parameter parameter) {
        Optional<ParameterResolver> resolver = resolvers.stream()
                .filter(r -> r.supports(parameter))
                .findFirst();

        if (resolver.isPresent()) {
            return resolver.get();
        } else {
            throw new IllegalStateException("No resolver found for parameter: " + parameter.getName());
        }
    }
}
