package com.fire.controller;

import com.fire.common.router.core.RouteMatch;
import com.fire.common.router.core.Router;
import com.fire.common.router.enums.HttpMethod;
import com.fire.common.router.handler.HandlerInvoker;
import com.fire.common.router.handler.HandlerMethod;
import com.fire.common.router.interfaces.Controller;
import com.fire.common.router.interfaces.RequestMapping;
import com.fire.common.router.interfaces.ResponseBody;
import com.fire.common.router.interceptor.LoggingInterceptor;
import com.fire.common.router.interceptor.CorsInterceptor;
import com.fire.util.ClasspathScanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {

    private Router router;
    private HandlerInvoker handlerInvoker;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.router = new Router();
        this.handlerInvoker = new HandlerInvoker();

        // 注册拦截器（类似Gin的中间件）
        this.handlerInvoker.addInterceptor(new CorsInterceptor());
        this.handlerInvoker.addInterceptor(new LoggingInterceptor());

        // 从web.xml获取扫描包路径
        String scanPackage = config.getInitParameter("scanPackage");

        try {
            // 使用工具类扫描，更清晰
            Set<Class<?>> classes = ClasspathScanner.scan(scanPackage);
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    registerController(clazz);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Initialization failed", e);
        }
    }

    private void registerController(Class<?> clazz) throws Exception {
        Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                HandlerMethod handler = new HandlerMethod(controllerInstance, method);
                router.addRoute(mapping.method(), mapping.path(), handler);

                String responseType = method.isAnnotationPresent(ResponseBody.class) ? "application/json" : "text/html";
                System.out.println("Mapped " + mapping.method() + " " + mapping.path() + " -> " + clazz.getSimpleName()
                        + "." + method.getName() + " (Response Type: " + responseType + ")");
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpMethod method = HttpMethod.valueOf(req.getMethod().toUpperCase());
        Optional<RouteMatch> routeMatch = router.findRoute(method, path);

        if (routeMatch.isPresent()) {
            try {
                handlerInvoker.invoke(req, resp, routeMatch.get());

            } catch (Exception e) {
                e.printStackTrace();
                handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
            }
        } else {
            handleError(resp, HttpServletResponse.SC_NOT_FOUND, "Not Found: " + method + " " + path);
        }
    }

    // 统一错误处理方法
    private void handleError(HttpServletResponse resp, int status, String message) {
        try {
            resp.setStatus(status);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"error\":\"" + message + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
