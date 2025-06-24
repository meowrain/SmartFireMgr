package com.fire.common.router.handler;

import com.fire.common.router.core.RouteMatch;
import com.fire.common.router.handler.resolver.ParameterResolver;
import com.fire.common.router.handler.resolver.ReturnValueHandler;
import com.fire.common.router.handler.resolver.param.PathVariableParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestParameterResolver;
import com.fire.common.router.handler.resolver.param.ResponseParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestBodyParameterResolver;
import com.fire.common.router.handler.resolver.param.RequestParamParameterResolver;
import com.fire.common.router.handler.resolver.returnvalue.ResponseBodyReturnValueHandler;
import com.fire.common.router.handler.resolver.returnvalue.DefaultReturnValueHandler;
import com.fire.common.router.interceptor.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HandlerInvoker {
    private final List<ParameterResolver> parameterResolvers = new ArrayList<>();
    private final List<ReturnValueHandler> returnValueHandlers = new ArrayList<>();
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    public HandlerInvoker() {
        // 注册所有参数解析器
        parameterResolvers.add(new RequestParameterResolver());
        parameterResolvers.add(new ResponseParameterResolver());
        parameterResolvers.add(new PathVariableParameterResolver());
        parameterResolvers.add(new RequestBodyParameterResolver());
        parameterResolvers.add(new RequestParamParameterResolver());

        // 注册返回值处理器（注意顺序：优先级高的在前面）
        returnValueHandlers.add(new ResponseBodyReturnValueHandler());
        returnValueHandlers.add(new DefaultReturnValueHandler()); // 默认处理器放最后
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response, RouteMatch routeMatch)
            throws Exception {
        HandlerMethod handlerMethod = routeMatch.getHandlerMethod();
        Method method = handlerMethod.getMethod();
        Exception exception = null;

        try {
            // 执行前置拦截器
            for (HandlerInterceptor interceptor : interceptors) {
                if (!interceptor.preHandle(request, response, handlerMethod)) {
                    return; // 中断处理
                }
            }

            // 解析方法参数
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                args[i] = findParameterResolver(parameter)
                        .resolve(parameter, request, response, routeMatch);
            }

            // 调用方法并获取返回值
            Object returnValue = method.invoke(handlerMethod.getControllerBean(), args);

            // 处理返回值
            ReturnValueHandler returnValueHandler = findReturnValueHandler(method);
            returnValueHandler.handleReturnValue(returnValue, method, request, response);

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

    private ParameterResolver findParameterResolver(Parameter parameter) {
        Optional<ParameterResolver> resolver = parameterResolvers.stream()
                .filter(r -> r.supports(parameter))
                .findFirst();

        if (resolver.isPresent()) {
            return resolver.get();
        } else {
            throw new IllegalStateException("No resolver found for parameter: " + parameter.getName());
        }
    }

    private ReturnValueHandler findReturnValueHandler(Method method) {
        Optional<ReturnValueHandler> handler = returnValueHandlers.stream()
                .filter(h -> h.supports(method))
                .findFirst();

        if (handler.isPresent()) {
            return handler.get();
        } else {
            throw new IllegalStateException("No return value handler found for method: " + method.getName());
        }
    }
}
