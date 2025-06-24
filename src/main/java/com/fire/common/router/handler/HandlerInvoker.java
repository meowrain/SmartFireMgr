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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 处理器调用器 - 路由框架的核心执行引擎
 * <p>
 * 主要职责：
 * 1. 管理参数解析器链，自动解析Controller方法参数
 * 2. 管理返回值处理器链，统一处理方法返回值
 * 3. 管理拦截器链，提供AOP功能（前置、后置、完成后处理）
 * 4. 协调整个请求处理流程，确保按正确顺序执行
 * <p>
 * 设计模式：
 * - 责任链模式：参数解析器链、返回值处理器链
 * - 策略模式：不同类型参数使用不同解析策略
 * - 模板方法模式：定义固定的请求处理流程模板
 *
 */
public class HandlerInvoker {
    private static final Logger log = LoggerFactory.getLogger(HandlerInvoker.class);
    /**
     * 参数解析器列表
     * <p>
     * 这里使用了责任链模式，所有的参数解析器都注册到一个列表中，按顺序执行
     */
    private final List<ParameterResolver> parameterResolvers = new ArrayList<>();
    /**
     * 返回值处理器列表
     */
    private final List<ReturnValueHandler> returnValueHandlers = new ArrayList<>();
    /**
     * 拦截器列表
     */
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    public HandlerInvoker() {

        // 注册所有参数解析器
        addParameterResolver(new RequestParameterResolver());
        addParameterResolver(new ResponseParameterResolver());
        addParameterResolver(new PathVariableParameterResolver());
        addParameterResolver(new RequestBodyParameterResolver());
        addParameterResolver(new RequestParamParameterResolver());

        // 注册返回值处理器（注意顺序：优先级高的在前面）
        addReturnValueHandler(new ResponseBodyReturnValueHandler());
        addReturnValueHandler(new DefaultReturnValueHandler());// 默认处理器放最后
    }

    /**
     * 添加自定义参数解析器
     *
     * @param resolver 自定义参数解析器
     */
    public void addParameterResolver(ParameterResolver resolver) {
        parameterResolvers.add(resolver);
    }

    /**
     * 添加自定义返回值处理器
     *
     * @param handler 自定义返回值处理器
     */
    public void addReturnValueHandler(ReturnValueHandler handler) {
        returnValueHandlers.add(handler);
    }

    /**
     * 添加自定义拦截器
     *
     * @param interceptor 自定义拦截器
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 执行请求处理流程
     * <p>
     * 1. 执行前置拦截器
     * 2. 解析方法参数
     * 3. 调用Controller方法
     * 4. 处理返回值
     * 5. 执行后置拦截器
     * 6. 执行完成拦截器
     * 异常处理在这里完成统一捕获
     * 
     * @param request    {@link HttpServletRequest} HTTP请求对象
     * @param response   {@link HttpServletResponse} HTTP响应对象
     * @param routeMatch {@link RouteMatch} 路由匹配信息，包含处理方法和控制器实例
     * @throws Exception
     */
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
                // 为每个参数找到对应的解析器，并解析参数值
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
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private ParameterResolver findParameterResolver(Parameter parameter) {
        // 找到支持该参数的解析器
        // 使用Optional避免NullPointerException
        Optional<ParameterResolver> resolver = parameterResolvers.stream()
                .filter(r -> r.supports(parameter))
                .findFirst();
        // 如果找到了解析器，则返回它，否则抛出异常
        if (resolver.isPresent()) {
            return resolver.get();
        } else {
            throw new IllegalStateException("No resolver found for parameter: " + parameter.getName());
        }
    }

    /**
     * 查找支持给定方法的返回值处理器
     * 
     * @param method {@link Method} 要处理的控制器方法
     * 
     *               该方法会遍历所有注册的返回值处理器，找到第一个支持当前方法的处理器。
     *               如果没有找到合适的处理器，则抛出异常。
     * @return
     */
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
